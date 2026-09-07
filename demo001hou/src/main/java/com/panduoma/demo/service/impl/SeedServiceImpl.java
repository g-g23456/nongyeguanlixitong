package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panduoma.demo.entity.SeedAllocationResults;
import com.panduoma.demo.entity.SeedForecast;
import com.panduoma.demo.entity.SeedInventory;
import com.panduoma.demo.mapper.SeedAllocationResultsMapper;
import com.panduoma.demo.mapper.SeedForecastMapper;
import com.panduoma.demo.mapper.SeedInventoryMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.SeedService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class SeedServiceImpl implements SeedService {

    @Resource
    private SeedInventoryMapper seedInventoryMapper;

    @Resource
    private SeedAllocationResultsMapper seedAllocationResultsMapper;

    @Resource
    private SeedForecastMapper seedForecastMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;

    @Override
    public Result<?> seedInventory(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        // 分页查询
        Page<SeedInventory> resultPage = seedInventoryMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<SeedInventory>().orderByAsc("sku"));

        long total = resultPage.getTotal();
        List<SeedInventory> records = resultPage.getRecords();

        // 全量统计：种类数 + 库存总量
        List<SeedInventory> allRecords = seedInventoryMapper.selectList(new QueryWrapper<>());
        long categoryCount = allRecords.size();
        BigDecimal totalInventory = allRecords.stream()
                .map(r -> r.getInventory() != null ? r.getInventory() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // 构建 stats
        List<Map<String, Object>> stats = new ArrayList<>();
        stats.add(Map.of("name", "物资种类", "value", categoryCount, "unit", "种"));

        Map<String, Object> inventoryStat = new LinkedHashMap<>();
        inventoryStat.put("name", "库存总量");
        inventoryStat.put("value", totalInventory);
        inventoryStat.put("unit", "吨");
        inventoryStat.put("warning", true);
        stats.add(inventoryStat);

        stats.add(Map.of("name", "本月入库", "value", 0, "unit", "吨"));
        stats.add(Map.of("name", "本月出库", "value", 0, "unit", "吨"));

        // 构建 items
        List<Map<String, Object>> items = new ArrayList<>();
        for (SeedInventory record : records) {
            BigDecimal stock = record.getInventory() != null ? record.getInventory() : BigDecimal.ZERO;
            BigDecimal threshold = record.getSafetyThreshold() != null ? record.getSafetyThreshold() : BigDecimal.ZERO;
            boolean belowThreshold = stock.compareTo(threshold) < 0;

            String expiry = record.getStatMouth() != null
                    ? record.getStatMouth().getYear() + "-" + String.format("%02d", record.getStatMouth().getMonthValue())
                    : "";

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", record.getId());
            item.put("code", record.getSku());
            item.put("name", record.getProductName());
            item.put("type", record.getCategory());
            item.put("stock", stock);
            item.put("threshold", threshold);
            item.put("unit", "吨");
            item.put("expiry", expiry);
            item.put("aiAlert", belowThreshold ? "低于阈值" : "充足");
            item.put("status", record.getStatus() != null ? record.getStatus() : (belowThreshold ? "补货" : "正常"));
            items.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("stats", stats);
        data.put("items", items);
        return Result.data(data);
    }

    @Override
    public Result<?> seedCreate(Map<String, Object> request) {
        String sku = request.get("code") != null ? request.get("code").toString() : null;
        String name = request.get("name") != null ? request.get("name").toString() : null;
        String type = request.get("type") != null ? request.get("type").toString() : null;
        BigDecimal stock = request.get("stock") != null ? new BigDecimal(request.get("stock").toString()) : BigDecimal.ZERO;
        BigDecimal threshold = request.get("threshold") != null ? new BigDecimal(request.get("threshold").toString()) : BigDecimal.ZERO;
        String status = request.get("status") != null ? request.get("status").toString() : null;

        // 校验物资编号是否已存在
        if (sku != null) {
            QueryWrapper<SeedInventory> qw = new QueryWrapper<>();
            qw.eq("sku", sku);
            Long count = seedInventoryMapper.selectCount(qw);
            if (count > 0) {
                return Result.error(400, "物资编号已存在");
            }
        }

        // 构建实体
        SeedInventory entity = SeedInventory.builder()
                .sku(sku)
                .productName(name)
                .category(type)
                .inventory(stock)
                .safetyThreshold(threshold)
                .status(status)
                .statMouth(LocalDate.now().withDayOfMonth(1))
                .build();

        seedInventoryMapper.insert(entity);

        // 构建返回数据
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", entity.getId());
        data.put("sku", entity.getSku());
        data.put("product_name", entity.getProductName());
        data.put("category", entity.getCategory());
        data.put("inventory", entity.getInventory());
        data.put("safety_threshold", entity.getSafetyThreshold());
        data.put("status", entity.getStatus());
        data.put("stat_mouth", entity.getStatMouth());
        data.put("updated_at", entity.getUpdatedAt());

        return Result.data("入库登记成功", data);
    }

    @Override
    public Result<?> seedAllocation(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String crop = Objects.toString(request.get("crop"), "全部作物");
        String strategy = Objects.toString(request.get("strategy"), "按种植面积比例");

        try {
            // 查询当前库存数据
            List<SeedInventory> records = seedInventoryMapper.selectList(new QueryWrapper<>());

            // 构建 prompt
            String prompt = buildSeedAllocationPrompt(crop, strategy, records);
            Map<String, Object> deepseekResult = callDeepseek(prompt);

            // 将请求体和返回结果写入 seed_allocation_results
            try {
                saveAllocationResult(prompt, deepseekResult);
            } catch (Exception ex) {
                // 写入失败不影响主流程返回
                System.err.println("写入 seed_allocation_results 失败: " + ex.getMessage());
            }

            return Result.data("分配完成", deepseekResult);
        } catch (Exception e) {
            return Result.error("AI 农资分配执行失败：" + e.getMessage());
        }
    }

    private String buildSeedAllocationPrompt(String crop, String strategy, List<SeedInventory> records) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是农业农资优化专家。请根据当前农资库存数据，给出最优的农资需求分配方案。\n");
        prompt.append("作物范围：").append(crop).append("\n");
        prompt.append("分配策略：").append(strategy).append("\n\n");
        prompt.append("当前农资库存数据：\n");
        prompt.append(objectMapper.writeValueAsString(records)).append("\n\n");
        prompt.append("请输出严格的 JSON，字段必须包含：\n");
        prompt.append("categories: 字符串数组，物资类别名称列表（如[\"复合肥\",\"尿素\",\"杀虫剂\",\"种子\"]），从库存数据中提取。\n");
        prompt.append("legend: 字符串数组，作物名称列表（如[\"水稻\",\"小麦\",\"玉米\",\"大豆\"]）。\n");
        prompt.append("yAxisName: 字符串，单位（如\"kg\"）。\n");
        prompt.append("series: 数组，每个对象包含 name(作物名)、type(固定\"bar\")、stack(固定\"a\")、data(数组，长度与categories一致，表示该作物对每种物资的需求量，数值为合理正整数)。\n");
        prompt.append("tips: 字符串，包含两条优化建议，用\\n分隔，格式如\"化肥利用率提升 12.5%\\n成本降低: 8.6%\"。\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");
        return prompt.toString();
    }

    private Map<String, Object> callDeepseek(String prompt) {
        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            return buildFallbackAllocation();
        }

        try {
            String baseUrl = System.getProperty("deepseek.base-url", "https://api.deepseek.com");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", "deepseek-chat");
            payload.put("temperature", 0.2);
            payload.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                System.err.println("DeepSeek API 返回错误状态: " + response.statusCode() + ", 回退到本地策略");
                return buildFallbackAllocation();
            }

            Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
            if (choices == null || choices.isEmpty()) {
                return buildFallbackAllocation();
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = Objects.toString(message.get("content"), "").trim();
            if (content.startsWith("```")) {
                content = content.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
            }

            Map<String, Object> result = objectMapper.readValue(content, Map.class);
            return normalizeAllocationResult(result);
        } catch (Exception e) {
            System.err.println("DeepSeek API 调用失败: " + e.getMessage() + ", 回退到本地策略");
            return buildFallbackAllocation();
        }
    }

    private void saveAllocationResult(String prompt, Map<String, Object> aiResult) {
        long taskId = System.currentTimeMillis();

        String requestStr;
        String resultStr;
        try {
            requestStr = objectMapper.writeValueAsString(prompt);
        } catch (Exception e) {
            requestStr = prompt;
        }
        try {
            resultStr = objectMapper.writeValueAsString(aiResult);
        } catch (Exception e) {
            resultStr = aiResult.toString();
        }

        SeedAllocationResults entity = SeedAllocationResults.builder()
                .taskId(String.valueOf(taskId))
                .request(requestStr)
                .result(resultStr)
                .createdAt(LocalDateTime.now())
                .build();
        seedAllocationResultsMapper.insert(entity);
    }

    private Map<String, Object> buildFallbackAllocation() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("categories", List.of("复合肥", "尿素", "杀虫剂", "种子"));
        result.put("legend", List.of("水稻", "小麦", "玉米", "大豆"));
        result.put("yAxisName", "kg");
        List<Map<String, Object>> series = new ArrayList<>();
        series.add(Map.of("name", "水稻", "type", "bar", "stack", "a", "data", List.of(850, 620, 25, 35)));
        series.add(Map.of("name", "小麦", "type", "bar", "stack", "a", "data", List.of(720, 540, 18, 28)));
        series.add(Map.of("name", "玉米", "type", "bar", "stack", "a", "data", List.of(380, 280, 12, 15)));
        series.add(Map.of("name", "大豆", "type", "bar", "stack", "a", "data", List.of(230, 120, 9, 7)));
        result.put("series", series);
        result.put("tips", "化肥利用率提升 12.5%\n成本降低: 8.6%");
        return result;
    }

    private Map<String, Object> normalizeAllocationResult(Map<String, Object> result) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        normalized.put("categories", result.getOrDefault("categories", List.of()));
        normalized.put("legend", result.getOrDefault("legend", List.of()));
        normalized.put("yAxisName", result.getOrDefault("yAxisName", "kg"));
        normalized.put("series", result.getOrDefault("series", List.of()));
        normalized.put("tips", result.getOrDefault("tips", ""));
        return normalized;
    }

    @Override
    public Result<?> seedPredict() {
        // 查询全部预测数据
        List<SeedForecast> forecasts = seedForecastMapper.selectList(
                new QueryWrapper<SeedForecast>().orderByAsc("stat_mouth", "product_code"));

        // 查询 seed_inventory 建立 product_code -> product_name 映射
        List<SeedInventory> inventories = seedInventoryMapper.selectList(new QueryWrapper<>());
        Map<String, String> skuToName = new HashMap<>();
        for (SeedInventory inv : inventories) {
            if (inv.getSku() != null) {
                skuToName.put(inv.getSku(), inv.getProductName());
            }
        }

        // 12 个月标签
        String[] months = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        List<String> trendMonths = Arrays.asList(months);

        // 按月汇总历史消耗和预测消耗
        BigDecimal[] historySum = new BigDecimal[12];
        BigDecimal[] predictSum = new BigDecimal[12];
        Arrays.fill(historySum, BigDecimal.ZERO);
        Arrays.fill(predictSum, BigDecimal.ZERO);

        // 按产品收集安全阈值（取最近月份的数据）
        Map<String, BigDecimal> productSafetyMap = new LinkedHashMap<>();
        // 总缺口和总需求用于生成 alert
        BigDecimal totalGap = BigDecimal.ZERO;
        BigDecimal totalExpected = BigDecimal.ZERO;
        BigDecimal totalInventory = BigDecimal.ZERO;

        for (SeedForecast f : forecasts) {
            int monthIdx = f.getStatMouth() != null ? f.getStatMouth().getMonthValue() - 1 : 0;
            if (monthIdx >= 0 && monthIdx < 12) {
                historySum[monthIdx] = historySum[monthIdx].add(
                        f.getHistoryConsumption() != null ? f.getHistoryConsumption() : BigDecimal.ZERO);
                predictSum[monthIdx] = predictSum[monthIdx].add(
                        f.getExpectedConsumption() != null ? f.getExpectedConsumption() : BigDecimal.ZERO);
            }

            String productName = skuToName.getOrDefault(f.getProductCode(), f.getProductCode());
            // 保留每个产品最新的 safety_threshold
            productSafetyMap.put(productName,
                    f.getSafetyThreshold() != null ? f.getSafetyThreshold() : BigDecimal.ZERO);

            totalGap = totalGap.add(f.getGapQuantity() != null ? f.getGapQuantity() : BigDecimal.ZERO);
            totalExpected = totalExpected.add(f.getExpectedConsumption() != null ? f.getExpectedConsumption() : BigDecimal.ZERO);
            totalInventory = totalInventory.add(f.getCurrentInventory() != null ? f.getCurrentInventory() : BigDecimal.ZERO);
        }

        // 构建 trend 折线数据
        List<BigDecimal> historyList = new ArrayList<>();
        List<BigDecimal> predictList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            historyList.add(historySum[i]);
            predictList.add(predictSum[i]);
        }

        List<Map<String, Object>> trend = new ArrayList<>();
        Map<String, Object> historyLine = new LinkedHashMap<>();
        historyLine.put("name", "历史消耗");
        historyLine.put("type", "line");
        historyLine.put("data", historyList);
        historyLine.put("smooth", true);
        trend.add(historyLine);

        Map<String, Object> predictLine = new LinkedHashMap<>();
        predictLine.put("name", "AI预测");
        predictLine.put("type", "line");
        predictLine.put("data", predictList);
        predictLine.put("smooth", true);
        predictLine.put("lineStyle", Map.of("type", "dashed"));
        trend.add(predictLine);

        // 构建雷达图数据
        List<Map<String, Object>> radarIndicator = new ArrayList<>();
        List<BigDecimal> radarValues = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : productSafetyMap.entrySet()) {
            radarIndicator.add(Map.of("name", entry.getKey(), "max", 100));
            // 将 safety_threshold 转为 0-100 的评分
            BigDecimal threshold = entry.getValue();
            BigDecimal score = threshold.compareTo(BigDecimal.ZERO) > 0
                    ? threshold.min(new BigDecimal("100"))
                    : BigDecimal.ZERO;
            radarValues.add(score);
        }

        List<Map<String, Object>> radar = new ArrayList<>();
        Map<String, Object> radarItem = new LinkedHashMap<>();
        radarItem.put("value", radarValues);
        radarItem.put("name", "当前库存安全阈值");
        radar.add(radarItem);

        // 生成 alert 文本
        String alert;
        if (totalGap.compareTo(BigDecimal.ZERO) > 0) {
            alert = String.format("基于LSTM时间序列预测，预计下季度农资总需求%skg，当前库存%skg，缺口%skg。AI建议：根据各产品缺口比例提前采购补货。",
                    totalExpected.stripTrailingZeros().toPlainString(),
                    totalInventory.stripTrailingZeros().toPlainString(),
                    totalGap.stripTrailingZeros().toPlainString());
        } else {
            alert = String.format("基于LSTM时间序列预测，当前库存充足，预计下季度农资总需求%skg，当前库存%skg，无缺口。",
                    totalExpected.stripTrailingZeros().toPlainString(),
                    totalInventory.stripTrailingZeros().toPlainString());
        }

        // 组装返回数据
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trendMonths", trendMonths);
        data.put("yAxisName", "kg");
        data.put("trend", trend);
        data.put("radarIndicator", radarIndicator);
        data.put("radar", radar);
        data.put("alert", alert);

        return Result.data("预测完成", data);
    }

    @Override
    public Result<?> seedUpdate(Map<String, Object> request) {
        if (request == null || request.get("id") == null) {
            return Result.error("农资ID不能为空");
        }
        Long id = ((Number) request.get("id")).longValue();
        SeedInventory existing = seedInventoryMapper.selectById(id);
        if (existing == null) {
            return Result.error("农资记录不存在");
        }

        if (request.containsKey("code")) existing.setSku(request.get("code").toString());
        if (request.containsKey("name")) existing.setProductName(request.get("name").toString());
        if (request.containsKey("type")) existing.setCategory(request.get("type").toString());
        if (request.containsKey("stock")) existing.setInventory(new BigDecimal(request.get("stock").toString()));
        if (request.containsKey("threshold")) existing.setSafetyThreshold(new BigDecimal(request.get("threshold").toString()));
        if (request.containsKey("status")) existing.setStatus(request.get("status").toString());

        seedInventoryMapper.updateById(existing);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> seedDelete(Long id) {
        if (id == null) {
            return Result.error("农资ID不能为空");
        }
        SeedInventory existing = seedInventoryMapper.selectById(id);
        if (existing == null) {
            return Result.error("农资记录不存在");
        }
        seedInventoryMapper.deleteById(id);
        return Result.success("删除成功");
    }
}