package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panduoma.demo.entity.Region;
import com.panduoma.demo.entity.WaterAIAnalysis;
import com.panduoma.demo.entity.WaterAnalysis;
import com.panduoma.demo.entity.WaterQuota;
import com.panduoma.demo.entity.WaterTotal;
import com.panduoma.demo.mapper.RegionMapper;
import com.panduoma.demo.mapper.WaterAIAnalysisMapper;
import com.panduoma.demo.mapper.WaterAnalysisMapper;
import com.panduoma.demo.mapper.WaterQuotaMapper;
import com.panduoma.demo.mapper.WaterTotalMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.WaterService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class WaterServiceImpl implements WaterService {

    @Resource
    private WaterQuotaMapper waterQuotaMapper;

    @Resource
    private WaterTotalMapper waterTotalMapper;

    @Resource
    private WaterAIAnalysisMapper waterAIAnalysisMapper;

    @Resource
    private WaterAnalysisMapper waterAnalysisMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;

    @Override
    public Result<?> waterQuota(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        Page<WaterQuota> resultPage = waterQuotaMapper.selectPage(new Page<>(page, size), new QueryWrapper<>());

        // 填充片区名称
        List<WaterQuota> records = resultPage.getRecords();
        Set<Long> regionIds = records.stream()
                .map(WaterQuota::getRegionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> regionNameMap = new HashMap<>();
        if (!regionIds.isEmpty()) {
            List<Region> regions = regionMapper.selectBatchIds(regionIds);
            regionNameMap = regions.stream()
                    .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));
        }

        // 组装 items 列表
        List<Map<String, Object>> items = new ArrayList<>();
        for (WaterQuota record : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", record.getId());
            item.put("area", record.getRegionId() != null
                    ? regionNameMap.getOrDefault(record.getRegionId(), "未知片区") : "未知片区");
            item.put("quota", record.getQuota());
            item.put("used", record.getUsed());
            item.put("remaining", record.getRemain());
            item.put("usageRate", record.getUsageRate());
            item.put("cropType", null);
            item.put("aiStatus", record.getAiEvaluate());
            item.put("status", record.getStatus());
            items.add(item);
        }

        // 从 water_total 表读取汇总数据
        WaterTotal waterTotal = waterTotalMapper.selectOne(new QueryWrapper<WaterTotal>().last("LIMIT 1"));

        Map<String, Object> data = new LinkedHashMap<>();
        if (waterTotal != null) {
            data.put("totalQuota", waterTotal.getTotalQuota());
            data.put("allocated", waterTotal.getUsedQuota());
            data.put("remaining", waterTotal.getResidueQuota());
            data.put("usageRate", waterTotal.getAllUsageRate());
        } else {
            data.put("totalQuota", 0);
            data.put("allocated", 0);
            data.put("remaining", 0);
            data.put("usageRate", 0);
        }
        data.put("items", items);
        return Result.data(data);
    }

    @Override
    public Result<?> waterStatus() {
        List<Region> regions = regionMapper.selectList(new QueryWrapper<>());

        List<Map<String, Object>> traditional = new ArrayList<>();
        List<BigDecimal> currentEfficiency = new ArrayList<>();

        for (Region region : regions) {
            String areaName = region.getName() != null ? region.getName() : "未知片区";

            List<WaterQuota> quotaList = waterQuotaMapper.selectList(
                    new QueryWrapper<WaterQuota>().eq("region_id", region.getId()));

            BigDecimal totalQuota = BigDecimal.ZERO;
            BigDecimal totalUsed = BigDecimal.ZERO;
            BigDecimal avgUsageRate = BigDecimal.ZERO;

            if (!quotaList.isEmpty()) {
                for (WaterQuota q : quotaList) {
                    totalQuota = totalQuota.add(q.getQuota() != null ? q.getQuota() : BigDecimal.ZERO);
                    totalUsed = totalUsed.add(q.getUsed() != null ? q.getUsed() : BigDecimal.ZERO);
                }
                avgUsageRate = totalQuota.compareTo(BigDecimal.ZERO) > 0
                        ? totalUsed.divide(totalQuota, 4, RoundingMode.HALF_UP)
                                .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                        : BigDecimal.ZERO;
            }

            Map<String, Object> tradItem = new LinkedHashMap<>();
            tradItem.put("area", areaName);
            tradItem.put("value", totalQuota);
            traditional.add(tradItem);

            currentEfficiency.add(avgUsageRate);
        }

        // efficiency: 5 个指标评分 [节水灌溉, 产量保障, 均衡性, 利用率, 可持续性]
        List<BigDecimal> efficiency = new ArrayList<>();
        for (Region region : regions) {
            List<WaterQuota> quotaList = waterQuotaMapper.selectList(
                    new QueryWrapper<WaterQuota>().eq("region_id", region.getId()));
            BigDecimal avgRate = BigDecimal.ZERO;
            if (!quotaList.isEmpty()) {
                BigDecimal sum = BigDecimal.ZERO;
                for (WaterQuota q : quotaList) {
                    sum = sum.add(q.getUsageRate() != null ? q.getUsageRate() : BigDecimal.ZERO);
                }
                avgRate = sum.divide(BigDecimal.valueOf(quotaList.size()), 2, RoundingMode.HALF_UP);
            }
            efficiency.add(avgRate);
        }
        // 补齐到 5 个元素
        while (efficiency.size() < 5) {
            efficiency.add(BigDecimal.ZERO);
        }
        if (efficiency.size() > 5) {
            efficiency = efficiency.subList(0, 5);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("traditional", traditional);
        data.put("currentEfficiency", currentEfficiency);
        data.put("efficiency", efficiency);
        return Result.data(data);
    }

    @Override
    public Result<?> waterAllocation(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String cycle = Objects.toString(request.get("cycle"), "季度分配");
        String goal = Objects.toString(request.get("goal"), "节水最大化");

        try {
            List<Region> allRegions = regionMapper.selectList(new QueryWrapper<>());

            List<Map<String, Object>> aggregatedRecords = new ArrayList<>();
            for (Region region : allRegions) {
                String areaName = region.getName() != null ? region.getName() : "未知片区";

                List<WaterQuota> quotaList = waterQuotaMapper.selectList(
                        new QueryWrapper<WaterQuota>().eq("region_id", region.getId()));

                BigDecimal totalQuota = BigDecimal.ZERO;
                BigDecimal totalUsed = BigDecimal.ZERO;
                BigDecimal totalRemain = BigDecimal.ZERO;
                BigDecimal avgUsageRate = BigDecimal.ZERO;

                if (!quotaList.isEmpty()) {
                    for (WaterQuota q : quotaList) {
                        totalQuota = totalQuota.add(q.getQuota() != null ? q.getQuota() : BigDecimal.ZERO);
                        totalUsed = totalUsed.add(q.getUsed() != null ? q.getUsed() : BigDecimal.ZERO);
                        totalRemain = totalRemain.add(q.getRemain() != null ? q.getRemain() : BigDecimal.ZERO);
                    }
                    avgUsageRate = totalQuota.compareTo(BigDecimal.ZERO) > 0
                            ? totalUsed.divide(totalQuota, 4, RoundingMode.HALF_UP)
                                    .multiply(BigDecimal.valueOf(100)).setScale(2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                }

                Map<String, Object> record = new LinkedHashMap<>();
                record.put("regionName", areaName);
                record.put("regionId", region.getId());
                record.put("quota", totalQuota);
                record.put("used", totalUsed);
                record.put("remain", totalRemain);
                record.put("usageRate", avgUsageRate);
                aggregatedRecords.add(record);
            }

            // 构建 prompt
            String prompt = buildWaterAllocationPrompt(cycle, goal, aggregatedRecords);
            Map<String, Object> deepseekResult = callDeepseek(prompt);

            // 将请求体和返回结果写入 water_ai_analysis
            try {
                saveAIAnalysis(prompt, deepseekResult);
            } catch (Exception ex) {
                System.err.println("写入 water_ai_analysis 失败: " + ex.getMessage());
            }

            return Result.data(deepseekResult);
        } catch (Exception e) {
            return Result.error("AI 水位分配执行失败：" + e.getMessage());
        }
    }

    private void saveAIAnalysis(String prompt, Map<String, Object> aiResult) {
        long taskId = System.currentTimeMillis();

        // 从 efficiency 数组提取 5 个雷达图评分
        List<?> efficiency = (List<?>) aiResult.getOrDefault("efficiency", List.of(0, 0, 0, 0, 0));
        BigDecimal waterSavingIrrigation = toBigDecimal(efficiency.size() > 0 ? efficiency.get(0) : 0);
        BigDecimal yieldGuarantee    = toBigDecimal(efficiency.size() > 1 ? efficiency.get(1) : 0);
        BigDecimal balance           = toBigDecimal(efficiency.size() > 2 ? efficiency.get(2) : 0);
        BigDecimal utilizationRate   = toBigDecimal(efficiency.size() > 3 ? efficiency.get(3) : 0);
        BigDecimal sustainability    = toBigDecimal(efficiency.size() > 4 ? efficiency.get(4) : 0);

        String resultJson;
        try {
            resultJson = objectMapper.writeValueAsString(aiResult);
        } catch (Exception e) {
            resultJson = aiResult.toString();
        }

        WaterAIAnalysis analysis = WaterAIAnalysis.builder()
                .taskId(taskId)
                .regionId(0L)
                .request(prompt)
                .result(resultJson)
                .waterSavingIrrigation(waterSavingIrrigation)
                .sustainability(sustainability)
                .utilizationRate(utilizationRate)
                .balance(balance)
                .yieldGuarantee(yieldGuarantee)
                .createdAt(LocalDateTime.now())
                .build();
        waterAIAnalysisMapper.insert(analysis);
    }

    private BigDecimal toBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof Number) return new BigDecimal(value.toString());
        try {
            return new BigDecimal(value.toString());
        } catch (Exception e) {
            return BigDecimal.ZERO;
        }
    }

    private String buildWaterAllocationPrompt(String cycle, String goal, List<Map<String, Object>> records) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是农业水资源优化专家。请根据当前水位配额数据，给出最优水位分配方案。\n");
        prompt.append("分配周期：").append(cycle).append("\n");
        prompt.append("优化目标：").append(goal).append("\n\n");
        prompt.append("当前水位配额数据：\n");
        prompt.append(objectMapper.writeValueAsString(records)).append("\n\n");
        prompt.append("请输出严格的 JSON，字段必须包含：\n");
        prompt.append("waterSaved: 数字，表示节约水量百分比。\n");
        prompt.append("yieldIncrease: 数字，表示增产百分比。\n");
        prompt.append("plan: 数组，每个对象包含 area(片区名) 和 value(分配水量)，片区名必须与数据中的一致。\n");
        prompt.append("efficiency: 数组，固定 5 个数字，分别表示[节水灌溉, 产量保障, 均衡性, 利用率, 可持续性]的评分(0-100)。\n");
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

    private Map<String, Object> buildFallbackAllocation() {
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("waterSaved", 8.3);
        result.put("yieldIncrease", 9.2);

        List<Map<String, Object>> plan = new ArrayList<>();
        List<Region> regions = regionMapper.selectList(new QueryWrapper<>());
        for (Region region : regions) {
            List<WaterQuota> quotaList = waterQuotaMapper.selectList(
                    new QueryWrapper<WaterQuota>().eq("region_id", region.getId()));
            BigDecimal quota = BigDecimal.ZERO;
            for (WaterQuota q : quotaList) {
                quota = quota.add(q.getQuota() != null ? q.getQuota() : BigDecimal.ZERO);
            }
            plan.add(Map.of("area", region.getName() != null ? region.getName() : "未知片区",
                    "value", quota.intValue()));
        }
        if (plan.isEmpty()) {
            plan.add(Map.of("area", "东片区", "value", 800));
            plan.add(Map.of("area", "西片区", "value", 650));
        }
        result.put("plan", plan);

        result.put("efficiency", List.of(88, 92, 85, 90, 87));
        return result;
    }

    private Map<String, Object> normalizeAllocationResult(Map<String, Object> result) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        normalized.put("waterSaved", result.getOrDefault("waterSaved", 8.3));
        normalized.put("yieldIncrease", result.getOrDefault("yieldIncrease", 9.2));
        normalized.put("plan", result.getOrDefault("plan", buildFallbackAllocation().get("plan")));

        Object eff = result.get("efficiency");
        if (eff instanceof List && ((List<?>) eff).size() == 5) {
            normalized.put("efficiency", eff);
        } else {
            normalized.put("efficiency", List.of(88, 92, 85, 90, 87));
        }
        return normalized;
    }

    @Override
    public Result<?> waterAIAnalysisList(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        Page<WaterAIAnalysis> resultPage = waterAIAnalysisMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<WaterAIAnalysis>().orderByDesc("created_at"));

        List<WaterAIAnalysis> records = resultPage.getRecords();

        // 填充片区名称
        Set<Long> regionIds = records.stream()
                .map(WaterAIAnalysis::getRegionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        if (!regionIds.isEmpty()) {
            List<Region> regions = regionMapper.selectBatchIds(regionIds);
            Map<Long, String> regionNameMap = regions.stream()
                    .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));
            for (WaterAIAnalysis record : records) {
                if (record.getRegionId() != null) {
                    record.setRegionName(regionNameMap.get(record.getRegionId()));
                }
            }
        }

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("current", resultPage.getCurrent());
        pageResult.put("size", resultPage.getSize());
        pageResult.put("total", resultPage.getTotal());
        pageResult.put("records", records);
        return Result.data(pageResult);
    }

    @Override
    public Result<?> waterAnalysis(int year) {
        // 查询指定年份的用水分析数据
        List<WaterAnalysis> records = waterAnalysisMapper.selectList(
                new QueryWrapper<WaterAnalysis>().eq("year", year));

        if (records == null || records.isEmpty()) {
            return Result.data(buildEmptyAnalysisResult());
        }

        // 填充片区名称
        Set<Long> regionIds = records.stream()
                .map(WaterAnalysis::getRegionId)
                .filter(Objects::nonNull)
                .collect(Collectors.toSet());
        Map<Long, String> regionNameMap = new HashMap<>();
        if (!regionIds.isEmpty()) {
            List<Region> regions = regionMapper.selectBatchIds(regionIds);
            regionNameMap = regions.stream()
                    .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));
        }

        String[] months = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        double[] actualSum = new double[12];
        double[] predictedSum = new double[12];
        boolean[] hasPredicted = new boolean[12];

        double totalActualAll = 0;
        double totalWasteAll = 0;

        List<Map<String, Object>> report = new ArrayList<>();

        for (WaterAnalysis record : records) {
            String regionName = record.getRegionId() != null
                    ? regionNameMap.getOrDefault(record.getRegionId(), "未知片区")
                    : "未知片区";

            // 解析 analysis_result JSON
            Map<String, Object> analysis;
            try {
                analysis = objectMapper.readValue(record.getAnalysisResult(), Map.class);
            } catch (Exception e) {
                analysis = new HashMap<>();
            }

            // 提取月度数据并累加到趋势图（兼容 mouthly/montly 拼写变体）
            List<Number> monthlyActual = (List<Number>) analysis.getOrDefault("monthlyActual",
                    analysis.getOrDefault("mouthlyActual", null));
            List<Number> monthlyPredicted = (List<Number>) analysis.getOrDefault("monthlyPredicted",
                    analysis.getOrDefault("mouthlyPredicted", null));
            for (int i = 0; i < 12; i++) {
                if (monthlyActual != null && i < monthlyActual.size() && monthlyActual.get(i) != null) {
                    actualSum[i] += monthlyActual.get(i).doubleValue();
                }
                if (monthlyPredicted != null && i < monthlyPredicted.size() && monthlyPredicted.get(i) != null) {
                    predictedSum[i] += monthlyPredicted.get(i).doubleValue();
                    hasPredicted[i] = true;
                }
            }

            // 提取片区报告字段
            double actualUsage = toDouble(analysis.get("actualUsage"));
            double theoreticalDemand = toDouble(analysis.get("theoreticalDemand"));
            double wasteAmount = toDouble(analysis.get("wasteAmount"));
            double wasteRate = toDouble(analysis.get("wasteRate"));
            String aiSuggestion = Objects.toString(analysis.get("aiSuggestion"), "");
            double savingPotential = toDouble(analysis.get("savingPotential"));

            totalActualAll += actualUsage;
            totalWasteAll += wasteAmount;

            Map<String, Object> reportItem = new LinkedHashMap<>();
            reportItem.put("region", regionName);
            reportItem.put("actualUsage", actualUsage);
            reportItem.put("theoreticalDemand", theoreticalDemand);
            reportItem.put("wasteAmount", wasteAmount);
            reportItem.put("wasteRate", wasteRate);
            reportItem.put("aiSuggestion", aiSuggestion);
            reportItem.put("savingPotential", savingPotential);
            report.add(reportItem);
        }

        // 构建 trend
        List<Double> actualList = new ArrayList<>();
        List<Double> predictedList = new ArrayList<>();
        for (int i = 0; i < 12; i++) {
            actualList.add(actualSum[i]);
            predictedList.add(hasPredicted[i] ? predictedSum[i] : null);
        }

        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("months", Arrays.asList(months));
        trend.put("actual", actualList);
        trend.put("predicted", predictedList);

        // 构建 waste 饼图分布
        double efficient = totalActualAll - totalWasteAll;
        double minorWaste = totalWasteAll * 0.6;
        double severeWaste = totalWasteAll * 0.4;
        if (totalActualAll > 0) {
            efficient = round2(efficient / totalActualAll * 100);
            minorWaste = round2(minorWaste / totalActualAll * 100);
            severeWaste = round2(severeWaste / totalActualAll * 100);
        }

        List<Map<String, Object>> categories = new ArrayList<>();
        categories.add(Map.of("value", efficient, "name", "高效利用", "color", "#52c41a"));
        categories.add(Map.of("value", minorWaste, "name", "轻微浪费", "color", "#faad14"));
        categories.add(Map.of("value", severeWaste, "name", "严重浪费", "color", "#f5222d"));

        Map<String, Object> waste = new LinkedHashMap<>();
        waste.put("categories", categories);

        // 组装最终结果
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trend", trend);
        data.put("waste", waste);
        data.put("report", report);
        return Result.data(data);
    }

    private double toDouble(Object value) {
        if (value == null) return 0;
        if (value instanceof Number) return ((Number) value).doubleValue();
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }

    private double round2(double value) {
        return BigDecimal.valueOf(value).setScale(1, RoundingMode.HALF_UP).doubleValue();
    }

    private Map<String, Object> buildEmptyAnalysisResult() {
        String[] months = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("months", Arrays.asList(months));
        trend.put("actual", List.of(0,0,0,0,0,0,0,0,0,0,0,0));
        trend.put("predicted", Arrays.asList(null,null,null,null,null,null,null,null,null,null,null,null));

        Map<String, Object> waste = new LinkedHashMap<>();
        waste.put("categories", List.of(
                Map.of("value", 0, "name", "高效利用", "color", "#52c41a"),
                Map.of("value", 0, "name", "轻微浪费", "color", "#faad14"),
                Map.of("value", 0, "name", "严重浪费", "color", "#f5222d")
        ));

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trend", trend);
        data.put("waste", waste);
        data.put("report", List.of());
        return data;
    }

    @Override
    public Result<?> waterQuotaUpdate(Map<String, Object> request) {
        if (request == null || request.get("items") == null) {
            return Result.error("请求参数不能为空");
        }
        List<Map<String, Object>> items;
        try {
            items = (List<Map<String, Object>>) request.get("items");
        } catch (ClassCastException e) {
            return Result.error("items 格式错误");
        }
        if (items.isEmpty()) {
            return Result.error("items 不能为空");
        }

        // 构建片区名称 -> region_id 映射
        List<Region> allRegions = regionMapper.selectList(new QueryWrapper<>());
        Map<String, Long> nameToId = new HashMap<>();
        for (Region region : allRegions) {
            nameToId.put(region.getName(), region.getId());
        }

        int updatedCount = 0;
        for (Map<String, Object> item : items) {
            String area = Objects.toString(item.get("area"), "");
            Long regionId = nameToId.get(area);
            if (regionId == null) {
                continue;
            }

            BigDecimal newQuota;
            try {
                newQuota = new BigDecimal(Objects.toString(item.get("quota"), "0"));
            } catch (Exception e) {
                continue;
            }

            // 查找该片区对应的 water_quota 记录
            WaterQuota record = waterQuotaMapper.selectOne(
                    new QueryWrapper<WaterQuota>().eq("region_id", regionId).last("LIMIT 1"));
            if (record == null) {
                continue;
            }

            record.setQuota(newQuota);
            // 重新计算剩余配额
            if (record.getUsed() != null) {
                record.setRemain(newQuota.subtract(record.getUsed()));
            }
            // 重新计算使用率
            if (newQuota.compareTo(BigDecimal.ZERO) > 0 && record.getUsed() != null) {
                record.setUsageRate(record.getUsed()
                        .divide(newQuota, 4, RoundingMode.HALF_UP));
            }
            waterQuotaMapper.updateById(record);
            updatedCount++;
        }

        return Result.success("成功更新 " + updatedCount + " 条配额记录");
    }

    @Override
    public Result<?> waterQuotaDelete(Long id) {
        if (id == null) {
            return Result.error("配额ID不能为空");
        }
        WaterQuota existing = waterQuotaMapper.selectById(id);
        if (existing == null) {
            return Result.error("配额记录不存在");
        }
        waterQuotaMapper.deleteById(id);
        return Result.success("删除成功");
    }
}