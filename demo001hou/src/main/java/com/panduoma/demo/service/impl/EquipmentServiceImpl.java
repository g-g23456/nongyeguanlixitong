package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panduoma.demo.entity.AiResults;
import com.panduoma.demo.entity.DispatchCompareResult;
import com.panduoma.demo.entity.Equipment;
import com.panduoma.demo.entity.EquipmentAllocationResults;
import com.panduoma.demo.entity.EquipmentMaintenance;
import com.panduoma.demo.entity.MachineryDispatchResult;
import com.panduoma.demo.entity.PredictYield;
import com.panduoma.demo.entity.PredictResource;
import com.panduoma.demo.entity.Region;
import com.panduoma.demo.mapper.AiResultsMapper;
import com.panduoma.demo.mapper.DispatchCompareResultMapper;
import com.panduoma.demo.mapper.EquipmentMapper;
import com.panduoma.demo.mapper.EquipmentAllocationResultsMapper;
import com.panduoma.demo.mapper.EquipmentMaintenanceMapper;
import com.panduoma.demo.mapper.MachineryDispatchResultMapper;
import com.panduoma.demo.mapper.PredictYieldMapper;
import com.panduoma.demo.mapper.PredictResourceMapper;
import com.panduoma.demo.mapper.RegionMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.EquipmentService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EquipmentServiceImpl implements EquipmentService {

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private EquipmentMaintenanceMapper equipmentMaintenanceMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private MachineryDispatchResultMapper machineryDispatchResultMapper;

    @Resource
    private EquipmentAllocationResultsMapper equipmentAllocationResultsMapper;

    @Resource
    private DispatchCompareResultMapper dispatchCompareResultMapper;

    @Resource
    private AiResultsMapper aiResultsMapper;

    @Resource
    private PredictYieldMapper predictYieldMapper;

    @Resource
    private PredictResourceMapper predictResourceMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;

    @Override
    public Result<?> equipmentList(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        Page<Equipment> resultPage = equipmentMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<Equipment>().orderByAsc("equipment_code"));

        long total = resultPage.getTotal();
        List<Equipment> records = resultPage.getRecords();

        List<Region> allRegions = regionMapper.selectList(new QueryWrapper<>());
        Map<Long, String> regionNameMap = allRegions.stream()
                .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));

        List<Map<String, Object>> items = new ArrayList<>();
        for (Equipment record : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("dbId", record.getId());
            item.put("id", record.getEquipmentCode());
            item.put("name", record.getName());
            item.put("type", record.getType());
            item.put("area", regionNameMap.getOrDefault(record.getRegionId(), ""));
            item.put("eff", record.getEfficiency());
            item.put("status", record.getStatus());
            item.put("score", record.getScore());
            items.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("items", items);
        return Result.data(data);
    }

    @Override
    public Result<?> equipmentCreate(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String equipmentCode = request.get("equipmentCode") != null ? request.get("equipmentCode").toString() : null;
        String name = request.get("name") != null ? request.get("name").toString() : null;
        String type = request.get("type") != null ? request.get("type").toString() : null;
        String areaName = request.get("area") != null ? request.get("area").toString() : null;
        String efficiency = request.get("efficiency") != null ? request.get("efficiency").toString() : null;
        String status = request.get("status") != null ? request.get("status").toString() : null;
        Integer score = request.get("score") != null ? ((Number) request.get("score")).intValue() : null;

        // 校验编号是否已存在
        if (equipmentCode != null) {
            QueryWrapper<Equipment> qw = new QueryWrapper<>();
            qw.eq("equipment_code", equipmentCode);
            Long count = equipmentMapper.selectCount(qw);
            if (count > 0) {
                return Result.error(400, "设备编号已存在");
            }
        }

        // 根据片区名称查找 region_id
        Long regionId = null;
        if (areaName != null) {
            QueryWrapper<Region> regionQw = new QueryWrapper<>();
            regionQw.eq("name", areaName);
            Region region = regionMapper.selectOne(regionQw);
            if (region != null) {
                regionId = region.getId();
            }
        }

        Equipment entity = Equipment.builder()
                .equipmentCode(equipmentCode)
                .name(name)
                .type(type)
                .regionId(regionId)
                .efficiency(efficiency)
                .status(status)
                .score(score)
                .build();
        equipmentMapper.insert(entity);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", entity.getId());
        data.put("equipmentCode", equipmentCode);
        data.put("name", name);
        data.put("type", type);
        data.put("area", areaName);
        data.put("efficiency", efficiency);
        data.put("status", status);
        data.put("score", score);
        return Result.data("新增设备成功", data);
    }

    @Override
    public Result<?> equipmentStatus() {
        // 查询全部调配记录
        List<MachineryDispatchResult> records = machineryDispatchResultMapper.selectList(new QueryWrapper<>());

        // 饼图数据：按 device_type 聚合 number 求和
        Map<String, Long> pieMap = new LinkedHashMap<>();
        for (MachineryDispatchResult r : records) {
            String type = r.getDeviceType() != null ? r.getDeviceType() : "未知";
            long num = r.getNumber() != null ? r.getNumber() : 0L;
            pieMap.merge(type, num, Long::sum);
        }
        List<Map<String, Object>> pieData = new ArrayList<>();
        for (Map.Entry<String, Long> entry : pieMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", entry.getValue());
            item.put("name", entry.getKey());
            pieData.add(item);
        }

        // 柱状图数据：按片区聚合平均利用率
        List<Region> allRegions = regionMapper.selectList(new QueryWrapper<>());
        Map<Long, String> regionNameMap = allRegions.stream()
                .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));

        Map<Long, List<BigDecimal>> regionRateMap = new LinkedHashMap<>();
        for (MachineryDispatchResult r : records) {
            if (r.getRegionId() != null && r.getUtilizationRate() != null) {
                regionRateMap.computeIfAbsent(r.getRegionId(), k -> new ArrayList<>())
                        .add(r.getUtilizationRate());
            }
        }

        List<String> barCategories = new ArrayList<>();
        List<BigDecimal> barData = new ArrayList<>();
        for (Map.Entry<Long, List<BigDecimal>> entry : regionRateMap.entrySet()) {
            Long regionId = entry.getKey();
            List<BigDecimal> rates = entry.getValue();
            BigDecimal sum = rates.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
            BigDecimal avg = sum.divide(new BigDecimal(rates.size()), 2, BigDecimal.ROUND_HALF_UP);
            barCategories.add(regionNameMap.getOrDefault(regionId, "未知"));
            barData.add(avg);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("pieData", pieData);
        data.put("barCategories", barCategories);
        data.put("barData", barData);
        return Result.data(data);
    }

    @Override
    public Result<?> machineryDispatchCreate(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }

        String task = request.get("task") != null ? request.get("task").toString() : "农机调配";
        String area = request.get("area") != null ? request.get("area").toString() : "全部片区";

        try {
            // 查询相关数据表构建上下文
            List<Equipment> allEquipment = equipmentMapper.selectList(new QueryWrapper<>());
            List<Region> allRegions = regionMapper.selectList(new QueryWrapper<>());
            List<MachineryDispatchResult> existingResults = machineryDispatchResultMapper.selectList(new QueryWrapper<>());
            Map<Long, String> regionNameMap = allRegions.stream()
                    .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));

            // 构建提示词
            String prompt = buildDispatchPrompt(task, area, allEquipment, allRegions, existingResults, regionNameMap);

            // 调用 DeepSeek API
            Map<String, Object> aiResult = callDeepseekForDispatch(prompt);

            // 将 AI 结果写入 machinery_dispatch_result 表
            saveDispatchResult(aiResult, allRegions, regionNameMap);

            // 将请求和结果持久化到 equipment_allocation_results 表
            saveAllocationRecord(prompt, aiResult);

            return Result.data(aiResult);
        } catch (Exception e) {
            // 异常时返回降级数据
            Map<String, Object> fallback = buildFallbackDispatch();
            return Result.data(fallback);
        }
    }

    private String buildDispatchPrompt(String task, String area,
                                       List<Equipment> equipmentList,
                                       List<Region> regions,
                                       List<MachineryDispatchResult> existingResults,
                                       Map<Long, String> regionNameMap) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个农业农机智能调配优化系统。\n");
        prompt.append("当前任务：").append(task).append("\n");
        prompt.append("调度模式：").append(area).append("\n\n");

        prompt.append("当前片区列表：\n");
        for (Region r : regions) {
            prompt.append("- ").append(r.getId()).append(": ").append(r.getName()).append("\n");
        }
        prompt.append("\n");

        prompt.append("当前农机设备台账：\n");
        for (Equipment e : equipmentList) {
            prompt.append("- 编号:").append(e.getEquipmentCode())
                    .append(", 名称:").append(e.getName())
                    .append(", 类型:").append(e.getType())
                    .append(", 片区:").append(regionNameMap.getOrDefault(e.getRegionId(), "未知"))
                    .append(", 效率:").append(e.getEfficiency())
                    .append(", 状态:").append(e.getStatus())
                    .append(", 评分:").append(e.getScore())
                    .append("\n");
        }
        prompt.append("\n");

        if (!existingResults.isEmpty()) {
            prompt.append("历史调配记录：\n");
            for (MachineryDispatchResult r : existingResults) {
                prompt.append("- 片区:").append(regionNameMap.getOrDefault(r.getRegionId(), "未知"))
                        .append(", 设备类型:").append(r.getDeviceType())
                        .append(", 数量:").append(r.getNumber())
                        .append(", 利用率:").append(r.getUtilizationRate())
                        .append("%\n");
            }
            prompt.append("\n");
        }

        prompt.append("请根据以上数据，为任务「").append(task).append("」在「").append(area).append("」模式下进行农机智能调配优化。\n");
        prompt.append("请输出严格的 JSON，字段必须包含：pieData、barCategories、barData、distanceReduction、efficiencyImprovement。\n");
        prompt.append("pieData: 数组，每个对象包含 value(整数) 和 name(设备类型名)，设备类型必须为：收割设备、耕作设备、播种设备、灌溉设备、植保设备。value 表示该类型设备调配数量。\n");
        prompt.append("barCategories: 数组，片区名称列表。\n");
        prompt.append("barData: 数组，与 barCategories 等长，每个值为该片区的设备利用率（整数，70-99之间）。\n");
        prompt.append("distanceReduction: 数字，表示路径优化后运输距离减少的百分比（如 23.5 表示减少 23.5%%）。\n");
        prompt.append("efficiencyImprovement: 数字，表示效率提升的百分比。\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");
        return prompt.toString();
    }

    private Map<String, Object> callDeepseekForDispatch(String prompt) {
        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            return buildFallbackDispatch();
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
                return buildFallbackDispatch();
            }

            Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
            if (choices == null || choices.isEmpty()) {
                return buildFallbackDispatch();
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = Objects.toString(message.get("content"), "").trim();
            if (content.startsWith("```")) {
                content = content.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
            }

            Map<String, Object> result = objectMapper.readValue(content, Map.class);
            return normalizeDispatchResult(result);
        } catch (Exception e) {
            System.err.println("DeepSeek API 调用失败: " + e.getMessage() + ", 回退到本地策略");
            return buildFallbackDispatch();
        }
    }

    private Map<String, Object> normalizeDispatchResult(Map<String, Object> result) {
        Map<String, Object> normalized = new LinkedHashMap<>();

        // pieData
        Object pieObj = result.get("pieData");
        if (pieObj instanceof List) {
            normalized.put("pieData", pieObj);
        } else {
            normalized.put("pieData", buildFallbackDispatch().get("pieData"));
        }

        // barCategories
        Object catObj = result.get("barCategories");
        if (catObj instanceof List) {
            normalized.put("barCategories", catObj);
        } else {
            normalized.put("barCategories", buildFallbackDispatch().get("barCategories"));
        }

        // barData
        Object barObj = result.get("barData");
        if (barObj instanceof List) {
            normalized.put("barData", barObj);
        } else {
            normalized.put("barData", buildFallbackDispatch().get("barData"));
        }

        // distanceReduction
        normalized.put("distanceReduction", result.getOrDefault("distanceReduction", 23.5));
        // efficiencyImprovement
        normalized.put("efficiencyImprovement", result.getOrDefault("efficiencyImprovement", 18.5));

        return normalized;
    }

    private void saveDispatchResult(Map<String, Object> aiResult, List<Region> regions, Map<Long, String> regionNameMap) {
        try {
            // 反向映射：片区名 → regionId
            Map<String, Long> nameToId = new HashMap<>();
            for (Region r : regions) {
                nameToId.put(r.getName(), r.getId());
            }

            List<Map<String, Object>> pieData = (List<Map<String, Object>>) aiResult.get("pieData");
            List<String> barCategories = (List<String>) aiResult.get("barCategories");
            List<Object> barData = (List<Object>) aiResult.get("barData");

            // 按片区和设备类型写入记录
            if (pieData != null && barCategories != null && barData != null) {
                for (Map<String, Object> pie : pieData) {
                    String deviceType = pie.get("name") != null ? pie.get("name").toString() : "未知";
                    long number = pie.get("value") != null ? ((Number) pie.get("value")).longValue() : 0L;

                    for (int i = 0; i < barCategories.size() && i < barData.size(); i++) {
                        String regionName = barCategories.get(i);
                        Long regionId = nameToId.get(regionName);
                        BigDecimal rate = barData.get(i) != null
                                ? new BigDecimal(barData.get(i).toString()) : BigDecimal.ZERO;

                        MachineryDispatchResult entity = MachineryDispatchResult.builder()
                                .regionId(regionId)
                                .deviceType(deviceType)
                                .number(number)
                                .utilizationRate(rate)
                                .build();
                        machineryDispatchResultMapper.insert(entity);
                    }
                }
            }
        } catch (Exception e) {
            // 保存失败不阻塞主流程
        }
    }

    private void saveAllocationRecord(String prompt, Map<String, Object> aiResult) {
        try {
            long taskId = System.currentTimeMillis();
            String requestStr = objectMapper.writeValueAsString(prompt);
            String resultStr = objectMapper.writeValueAsString(aiResult);

            EquipmentAllocationResults entity = EquipmentAllocationResults.builder()
                    .taskId(String.valueOf(taskId))
                    .request(requestStr)
                    .result(resultStr)
                    .createdAt(LocalDateTime.now())
                    .build();
            equipmentAllocationResultsMapper.insert(entity);
        } catch (Exception e) {
            // 保存失败不阻塞主流程
        }
    }

    private Map<String, Object> buildFallbackDispatch() {
        Map<String, Object> result = new LinkedHashMap<>();

        List<Map<String, Object>> pieData = new ArrayList<>();
        pieData.add(Map.of("value", 50, "name", "收割设备"));
        pieData.add(Map.of("value", 38, "name", "耕作设备"));
        pieData.add(Map.of("value", 35, "name", "播种设备"));
        pieData.add(Map.of("value", 28, "name", "灌溉设备"));
        pieData.add(Map.of("value", 15, "name", "植保设备"));
        result.put("pieData", pieData);

        result.put("barCategories", List.of("东区", "西区", "南区", "北区"));
        result.put("barData", List.of(95, 90, 88, 82));
        result.put("distanceReduction", 23.5);
        result.put("efficiencyImprovement", 18.5);
        return result;
    }

    @Override
    public Result<?> equipmentMaintenance() {
        List<EquipmentMaintenance> records = equipmentMaintenanceMapper.selectList(
                new QueryWrapper<EquipmentMaintenance>().orderByAsc("stat_mouth"));

        int totalPlanCount = 0;
        int totalManageCount = 0;
        int totalProceedCount = 0;
        int totalOverdueCount = 0;

        List<String> months = new ArrayList<>();
        List<BigDecimal> costs = new ArrayList<>();

        for (EquipmentMaintenance r : records) {
            totalPlanCount += r.getPlanCount() != null ? r.getPlanCount() : 0;
            totalManageCount += r.getManageCount() != null ? r.getManageCount() : 0;
            totalProceedCount += r.getProceedCount() != null ? r.getProceedCount() : 0;
            totalOverdueCount += r.getOverdueCount() != null ? r.getOverdueCount() : 0;

            if (r.getStatMouth() != null) {
                String[] parts = r.getStatMouth().split("-");
                if (parts.length == 2) {
                    months.add(Integer.parseInt(parts[1]) + "月");
                } else {
                    months.add(r.getStatMouth());
                }
            }
            costs.add(r.getTotalCost() != null ? r.getTotalCost() : BigDecimal.ZERO);
        }

        // stats：汇总维护统计
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("monthlyPlan", totalPlanCount);
        stats.put("completed", totalManageCount);
        stats.put("inProgress", totalProceedCount);
        stats.put("overdue", totalOverdueCount);

        // healthStatus：从 equipment 表按 status 分组统计
        List<Equipment> allEquipment = equipmentMapper.selectList(new QueryWrapper<>());
        Map<String, Long> statusCountMap = new LinkedHashMap<>();
        for (Equipment eq : allEquipment) {
            String status = eq.getStatus() != null ? eq.getStatus() : "未知";
            statusCountMap.merge(status, 1L, Long::sum);
        }

        // 状态 → 颜色映射
        Map<String, String> statusColorMap = new LinkedHashMap<>();
        statusColorMap.put("健康", "#52c41a");
        statusColorMap.put("正常", "#52c41a");
        statusColorMap.put("亚健康", "#faad14");
        statusColorMap.put("警告", "#faad14");
        statusColorMap.put("需维护", "#f5222d");
        statusColorMap.put("维修中", "#f5222d");

        List<Map<String, Object>> healthStatus = new ArrayList<>();
        for (Map.Entry<String, Long> entry : statusCountMap.entrySet()) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("value", entry.getValue());
            item.put("name", entry.getKey());
            Map<String, Object> itemStyle = new LinkedHashMap<>();
            itemStyle.put("color", statusColorMap.getOrDefault(entry.getKey(), "#1890ff"));
            item.put("itemStyle", itemStyle);
            healthStatus.add(item);
        }

        // costTrend：按月成本趋势
        List<BigDecimal> costValues = new ArrayList<>();
        for (BigDecimal cost : costs) {
            costValues.add(cost.setScale(1, BigDecimal.ROUND_HALF_UP));
        }

        Map<String, Object> costTrend = new LinkedHashMap<>();
        costTrend.put("months", months);
        costTrend.put("values", costValues);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("stats", stats);
        data.put("healthStatus", healthStatus);
        data.put("costTrend", costTrend);

        return Result.data(data);
    }

    @Override
    public Result<?> aiDecision(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String period = request.get("period") != null ? request.get("period").toString() : "2025年度";
        String dimension = request.get("dimension") != null ? request.get("dimension").toString() : "五维全优化";

        // 1. 从 dispatch_compare_result 表获取人工调配雷达数据
        List<DispatchCompareResult> dispatchRecords = dispatchCompareResultMapper.selectList(new QueryWrapper<>());
        List<Number> manualValues = new ArrayList<>();
        if (!dispatchRecords.isEmpty()) {
            DispatchCompareResult latest = dispatchRecords.get(dispatchRecords.size() - 1);
            manualValues.add(latest.getYieldBenefit() != null ? latest.getYieldBenefit().doubleValue() : 0);
            manualValues.add(latest.getWaterSave() != null ? latest.getWaterSave().doubleValue() : 0);
            manualValues.add(latest.getMaterialEfficiency() != null ? latest.getMaterialEfficiency().doubleValue() : 0);
            manualValues.add(latest.getLaborUtil() != null ? latest.getLaborUtil().doubleValue() : 0);
            manualValues.add(latest.getSoilProtect() != null ? latest.getSoilProtect().doubleValue() : 0);
            manualValues.add(latest.getRiskControl() != null ? latest.getRiskControl().doubleValue() : 0);
        } else {
            manualValues.addAll(List.of(75, 60, 70, 65, 72, 68));
        }

        try {
            // 2. 构建 DeepSeek 提示词
            String prompt = buildAiDecisionPrompt(period, dimension, manualValues);

            // 3. 调用 DeepSeek API
            Map<String, Object> aiResult = callDeepseekForAiDecision(prompt);

            // 4. 组装响应数据
            Map<String, Object> data = buildAiDecisionResponse(manualValues, aiResult);

            // 5. 保存结果到 ai_results 表
            saveAiResult("ai_decision", prompt, aiResult);

            return Result.data(data);
        } catch (Exception e) {
            // 降级返回默认数据
            Map<String, Object> data = buildAiDecisionResponse(manualValues, buildFallbackAiDecision());
            return Result.data(data);
        }
    }

    private String buildAiDecisionPrompt(String period, String dimension, List<Number> manualValues) {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个农业智能决策优化系统。\n");
        prompt.append("分析周期：").append(period).append("\n");
        prompt.append("优化维度：").append(dimension).append("\n\n");
        prompt.append("当前人工调配六维雷达数据（产量效益、节水能力、农资效率、人力利用、土壤保护、风险控制）：\n");
        prompt.append(manualValues.toString()).append("\n\n");
        prompt.append("请基于以上人工调配数据，进行 AI 智能优化分析，输出严格的 JSON，字段必须包含：\n");
        prompt.append("1. aiRadar: 数组，6个数字(0-100)，表示 AI 优化后的六维分值，顺序为：产量效益、节水能力、农资效率、人力利用、土壤保护、风险控制。AI 优化后的分值应高于人工调配。\n");
        prompt.append("2. convergenceRate: 字符串，收敛率百分比（如 98.6%）。\n");
        prompt.append("3. yieldIncrease: 数字，预计增产百分比（如 12.5）。\n");
        prompt.append("4. waterSaving: 数字，节水提升百分比（如 8.3）。\n");
        prompt.append("5. efficiencyIncrease: 数字，增效提升百分比（如 15.2）。\n");
        prompt.append("6. resourceUtilization: 数字，资源利用率（如 92）。\n");
        prompt.append("7. resourceAllocation: 数组，5个对象，每个包含 value(整数) 和 name(字符串)。name 必须为：耕地优化、水资源、农资分配、人力调度、器械调配。value 表示各项资源分配权重。\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");
        return prompt.toString();
    }

    private Map<String, Object> callDeepseekForAiDecision(String prompt) {
        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            return buildFallbackAiDecision();
        }

        try {
            String baseUrl = System.getProperty("deepseek.base-url", "https://api.deepseek.com");
            Map<String, Object> payload = new LinkedHashMap<>();
            payload.put("model", "deepseek-chat");
            payload.put("temperature", 0.2);
            payload.put("messages", List.of(Map.of("role", "user", "content", prompt)));

            HttpRequest httpRequest = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl + "/v1/chat/completions"))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .timeout(Duration.ofSeconds(60))
                    .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                    .build();

            HttpClient httpClient = HttpClient.newBuilder()
                    .connectTimeout(Duration.ofSeconds(30))
                    .build();
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() >= 400) {
                System.err.println("DeepSeek API 返回错误状态: " + response.statusCode() + ", 回退到本地策略");
                return buildFallbackAiDecision();
            }

            Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
            List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
            if (choices == null || choices.isEmpty()) {
                return buildFallbackAiDecision();
            }

            Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
            String content = Objects.toString(message.get("content"), "").trim();
            if (content.startsWith("```")) {
                content = content.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
            }

            return objectMapper.readValue(content, Map.class);
        } catch (Exception e) {
            System.err.println("DeepSeek API 调用失败: " + e.getMessage() + ", 回退到本地策略");
            return buildFallbackAiDecision();
        }
    }

    private Map<String, Object> buildAiDecisionResponse(List<Number> manualValues, Map<String, Object> aiResult) {
        Map<String, Object> data = new LinkedHashMap<>();

        data.put("convergenceRate", aiResult.getOrDefault("convergenceRate", "98.6%"));
        data.put("yieldIncrease", aiResult.getOrDefault("yieldIncrease", 12.5));
        data.put("waterSaving", aiResult.getOrDefault("waterSaving", 8.3));
        data.put("efficiencyIncrease", aiResult.getOrDefault("efficiencyIncrease", 15.2));
        data.put("resourceUtilization", aiResult.getOrDefault("resourceUtilization", 92));

        // 雷达图数据
        List<Map<String, Object>> radar = new ArrayList<>();

        // 人工调配
        Map<String, Object> manualRadar = new LinkedHashMap<>();
        manualRadar.put("value", manualValues);
        manualRadar.put("name", "人工调配");
        Map<String, Object> manualItemStyle = new LinkedHashMap<>();
        manualItemStyle.put("color", "#faad14");
        manualRadar.put("itemStyle", manualItemStyle);
        Map<String, Object> manualAreaStyle = new LinkedHashMap<>();
        manualAreaStyle.put("opacity", 0.2);
        manualRadar.put("areaStyle", manualAreaStyle);
        radar.add(manualRadar);

        // AI 优化
        Object aiRadarObj = aiResult.get("aiRadar");
        List<?> aiRadarValues = (aiRadarObj instanceof List) ? (List<?>) aiRadarObj : List.of(92, 88, 85, 82, 90, 91);
        Map<String, Object> aiRadar = new LinkedHashMap<>();
        aiRadar.put("value", aiRadarValues);
        aiRadar.put("name", "AI优化");
        Map<String, Object> aiItemStyle = new LinkedHashMap<>();
        aiItemStyle.put("color", "#52c41a");
        aiRadar.put("itemStyle", aiItemStyle);
        Map<String, Object> aiAreaStyle = new LinkedHashMap<>();
        aiAreaStyle.put("opacity", 0.3);
        aiRadar.put("areaStyle", aiAreaStyle);
        radar.add(aiRadar);

        data.put("radar", radar);

        // 资源分配
        Object resourceObj = aiResult.get("resourceAllocation");
        List<Map<String, Object>> resourceAllocation;
        if (resourceObj instanceof List) {
            resourceAllocation = (List<Map<String, Object>>) resourceObj;
        } else {
            resourceAllocation = buildDefaultResourceAllocation();
        }
        data.put("resourceAllocation", resourceAllocation);

        return data;
    }

    private List<Map<String, Object>> buildDefaultResourceAllocation() {
        List<Map<String, Object>> list = new ArrayList<>();
        list.add(Map.of("value", 32, "name", "耕地优化", "itemStyle", Map.of("color", "#52c41a")));
        list.add(Map.of("value", 25, "name", "水资源", "itemStyle", Map.of("color", "#1890ff")));
        list.add(Map.of("value", 20, "name", "农资分配", "itemStyle", Map.of("color", "#faad14")));
        list.add(Map.of("value", 15, "name", "人力调度", "itemStyle", Map.of("color", "#722ed1")));
        list.add(Map.of("value", 8, "name", "器械调配", "itemStyle", Map.of("color", "#eb2f96")));
        return list;
    }

    private Map<String, Object> buildFallbackAiDecision() {
        Map<String, Object> fallback = new LinkedHashMap<>();
        fallback.put("aiRadar", List.of(92, 88, 85, 82, 90, 91));
        fallback.put("convergenceRate", "98.6%");
        fallback.put("yieldIncrease", 12.5);
        fallback.put("waterSaving", 8.3);
        fallback.put("efficiencyIncrease", 15.2);
        fallback.put("resourceUtilization", 92);
        fallback.put("resourceAllocation", buildDefaultResourceAllocation());
        return fallback;
    }

    @Override
    public Result<?> yieldPrediction(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String year = request.get("year") != null ? request.get("year").toString() : String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String area = request.get("area") != null ? request.get("area").toString() : "全部";

        // 1. 构建查询条件：按年份（stat_mouth 前缀）和片区筛选
        QueryWrapper<PredictYield> qw = new QueryWrapper<>();
        qw.likeRight("stat_mouth", year);
        if (!"全部".equals(area)) {
            // 根据片区名称查 region_id
            QueryWrapper<Region> regionQw = new QueryWrapper<>();
            regionQw.eq("name", area);
            Region region = regionMapper.selectOne(regionQw);
            if (region != null) {
                qw.eq("region_id", region.getId());
            }
        }
        qw.orderByAsc("stat_mouth", "crop");
        List<PredictYield> records = predictYieldMapper.selectList(qw);

        // 2. 按作物聚合月度数据（1~12月）
        // crop -> monthIndex(0-11) -> sum value
        Map<String, BigDecimal[]> cropMonthlyMap = new LinkedHashMap<>();
        for (PredictYield r : records) {
            String crop = r.getCrop() != null ? r.getCrop() : "未知";
            int monthIdx = extractMonthIndex(r.getStatMouth());
            if (monthIdx < 0 || monthIdx > 11) continue;
            cropMonthlyMap.computeIfAbsent(crop, k -> {
                BigDecimal[] arr = new BigDecimal[12];
                Arrays.fill(arr, BigDecimal.ZERO);
                return arr;
            });
            BigDecimal val = r.getPredictedValue() != null ? r.getPredictedValue() : BigDecimal.ZERO;
            cropMonthlyMap.get(crop)[monthIdx] = cropMonthlyMap.get(crop)[monthIdx].add(val);
        }

        // 3. 确定已有数据的最大月份，找出需要 AI 预测的月份
        int maxDataMonth = 0;
        for (Map.Entry<String, BigDecimal[]> entry : cropMonthlyMap.entrySet()) {
            for (int i = 11; i >= 0; i--) {
                if (entry.getValue()[i].compareTo(BigDecimal.ZERO) > 0) {
                    maxDataMonth = Math.max(maxDataMonth, i + 1);
                    break;
                }
            }
        }
        // 如果表中无数据，默认到当前月
        if (maxDataMonth == 0) {
            maxDataMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }

        // 需要预测的月份数量（预测到12月，至少2个月，最多3个月）
        int predictMonths = Math.min(3, Math.max(2, 12 - maxDataMonth));
        List<String> predictMonthLabels = new ArrayList<>();
        for (int i = 1; i <= predictMonths; i++) {
            int m = maxDataMonth + i;
            if (m > 12) break;
            predictMonthLabels.add(m + "月");
        }

        // 4. 调用 DeepSeek 预测后续月份数据
        Map<String, List<BigDecimal>> aiPredicted = new LinkedHashMap<>();
        try {
            aiPredicted = callDeepseekForYieldPredict(year, area, cropMonthlyMap, maxDataMonth, predictMonthLabels);
        } catch (Exception e) {
            // AI 调用失败时，使用最后一个月数据衰减填充
            for (Map.Entry<String, BigDecimal[]> entry : cropMonthlyMap.entrySet()) {
                BigDecimal lastVal = BigDecimal.ZERO;
                for (int i = maxDataMonth - 1; i >= 0; i--) {
                    if (entry.getValue()[i].compareTo(BigDecimal.ZERO) > 0) {
                        lastVal = entry.getValue()[i];
                        break;
                    }
                }
                List<BigDecimal> predicted = new ArrayList<>();
                for (int i = 0; i < predictMonthLabels.size(); i++) {
                    predicted.add(lastVal);
                }
                aiPredicted.put(entry.getKey(), predicted);
            }
        }

        // 5. 拼接完整 12 个月趋势数据
        String[] monthLabels = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};
        List<String> months = Arrays.asList(monthLabels);

        Map<String, Object> trend = new LinkedHashMap<>();
        trend.put("months", months);

        // 作物名 -> 前端字段名映射（水稻->rice, 小麦->wheat, 玉米->corn）
        Map<String, String> cropKeyMap = new LinkedHashMap<>();
        cropKeyMap.put("水稻", "rice");
        cropKeyMap.put("小麦", "wheat");
        cropKeyMap.put("玉米", "corn");

        // 颜色映射
        Map<String, String> cropColorMap = new LinkedHashMap<>();
        cropColorMap.put("水稻", "#52c41a");
        cropColorMap.put("小麦", "#faad14");
        cropColorMap.put("玉米", "#1890ff");

        // 计算每种作物全年总值（用于饼图）
        Map<String, BigDecimal> cropTotalMap = new LinkedHashMap<>();

        // 如果数据库无数据，为三种作物添加默认空数组，确保前端图表正常渲染
        if (cropMonthlyMap.isEmpty()) {
            BigDecimal[] emptyArr = new BigDecimal[12];
            Arrays.fill(emptyArr, BigDecimal.ZERO);
            cropMonthlyMap.put("水稻", emptyArr.clone());
            cropMonthlyMap.put("小麦", emptyArr.clone());
            cropMonthlyMap.put("玉米", emptyArr.clone());
        }

        for (Map.Entry<String, BigDecimal[]> entry : cropMonthlyMap.entrySet()) {
            String crop = entry.getKey();
            String key = cropKeyMap.getOrDefault(crop, crop);
            BigDecimal[] monthlyValues = entry.getValue();

            // 拼接 AI 预测值到对应月份
            List<BigDecimal> predicted = aiPredicted.getOrDefault(crop, new ArrayList<>());
            for (int i = 0; i < predictMonthLabels.size() && i < predicted.size(); i++) {
                int targetMonth = maxDataMonth + i;
                if (targetMonth >= 0 && targetMonth < 12) {
                    monthlyValues[targetMonth] = predicted.get(i);
                }
            }

            // 转为 List<BigDecimal>
            List<BigDecimal> valuesList = new ArrayList<>();
            BigDecimal total = BigDecimal.ZERO;
            for (BigDecimal v : monthlyValues) {
                valuesList.add(v.setScale(0, BigDecimal.ROUND_HALF_UP));
                total = total.add(v);
            }
            trend.put(key, valuesList);
            cropTotalMap.put(crop, total);
        }

        // 6. 饼图数据：每种作物12月总值 + 占比
        BigDecimal grandTotal = cropTotalMap.values().stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        List<Map<String, Object>> pie = new ArrayList<>();
        for (Map.Entry<String, BigDecimal> entry : cropTotalMap.entrySet()) {
            String crop = entry.getKey();
            BigDecimal total = entry.getValue();
            // 取12月值作为饼图展示值
            BigDecimal[] monthlyValues = cropMonthlyMap.get(crop);
            int decIdx = 11;
            BigDecimal decValue = (decIdx >= 0 && decIdx < 12) ? monthlyValues[decIdx] : BigDecimal.ZERO;

            String pct = grandTotal.compareTo(BigDecimal.ZERO) > 0
                    ? total.multiply(new BigDecimal("100")).divide(grandTotal, 0, BigDecimal.ROUND_HALF_UP) + "%"
                    : "0%";

            Map<String, Object> pieItem = new LinkedHashMap<>();
            pieItem.put("value", decValue.setScale(0, BigDecimal.ROUND_HALF_UP));
            pieItem.put("name", crop + " " + pct);
            Map<String, Object> itemStyle = new LinkedHashMap<>();
            itemStyle.put("color", cropColorMap.getOrDefault(crop, "#1890ff"));
            pieItem.put("itemStyle", itemStyle);
            pie.add(pieItem);
        }

        // 7. 组装返回
        List<String> areaOptions = List.of("全部", "北区", "南区", "东区", "西区");
        List<String> yearOptions = List.of("2020", "2021", "2022", "2023", "2024", "2025", "2026");

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("trend", trend);
        data.put("pie", pie);
        data.put("areaOptions", areaOptions);
        data.put("yearOptions", yearOptions);

        return Result.data(data);
    }

    /**
     * 从 stat_mouth（格式 YYYY-MM）中提取月份索引（0-11）
     */
    private int extractMonthIndex(String statMouth) {
        if (statMouth == null) return -1;
        try {
            String[] parts = statMouth.split("-");
            if (parts.length == 2) {
                return Integer.parseInt(parts[1]) - 1;
            }
        } catch (NumberFormatException ignored) {
        }
        return -1;
    }

    /**
     * 调用 DeepSeek 预测后续月份产量数据
     */
    private Map<String, List<BigDecimal>> callDeepseekForYieldPredict(
            String year, String area,
            Map<String, BigDecimal[]> cropMonthlyMap,
            int maxDataMonth,
            List<String> predictMonthLabels) throws Exception {

        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("DeepSeek API Key 未配置");
        }

        // 构建提示词
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个农业产量预测专家系统。\n");
        prompt.append("分析年份：").append(year).append("\n");
        prompt.append("片区范围：").append(area).append("\n\n");
        prompt.append("当前已有 ").append(maxDataMonth).append(" 个月的产量数据（单位：kg）：\n");
        for (Map.Entry<String, BigDecimal[]> entry : cropMonthlyMap.entrySet()) {
            prompt.append(entry.getKey()).append(": [");
            BigDecimal[] vals = entry.getValue();
            for (int i = 0; i < maxDataMonth && i < 12; i++) {
                if (i > 0) prompt.append(", ");
                prompt.append(vals[i].stripTrailingZeros().toPlainString());
            }
            prompt.append("]\n");
        }
        prompt.append("\n请预测以下 ").append(predictMonthLabels.size()).append(" 个月的产量数据：");
        prompt.append(predictMonthLabels.toString()).append("\n");
        prompt.append("请输出严格的 JSON，key 为作物名称，value 为数组（长度 ").append(predictMonthLabels.size()).append("），值为合理的预测产量数值。\n");
        prompt.append("例如：{\"水稻\": [3900, 3920], \"小麦\": [2600, 2620], \"玉米\": [3500, 3520]}\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");

        String baseUrl = System.getProperty("deepseek.base-url", "https://api.deepseek.com");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("temperature", 0.3);
        payload.put("messages", List.of(Map.of("role", "user", "content", prompt.toString())));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("DeepSeek API 调用失败，HTTP 状态：" + response.statusCode());
        }

        Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("DeepSeek API 返回空结果");
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = Objects.toString(message.get("content"), "").trim();
        if (content.startsWith("```")) {
            content = content.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        }

        Map<String, Object> rawResult = objectMapper.readValue(content, Map.class);

        // 解析为 crop -> List<BigDecimal>
        Map<String, List<BigDecimal>> result = new LinkedHashMap<>();
        for (Map.Entry<String, Object> entry : rawResult.entrySet()) {
            String crop = entry.getKey();
            List<?> values = (List<?>) entry.getValue();
            List<BigDecimal> decimalValues = new ArrayList<>();
            for (Object v : values) {
                decimalValues.add(new BigDecimal(v.toString()));
            }
            result.put(crop, decimalValues);
        }
        return result;
    }

    private void saveAiResult(String taskId, String prompt, Map<String, Object> aiResult) {
        try {
            String requestStr = objectMapper.writeValueAsString(prompt);
            String resultStr = objectMapper.writeValueAsString(aiResult);

            AiResults entity = AiResults.builder()
                    .taskId(taskId + "_" + System.currentTimeMillis())
                    .request(requestStr)
                    .result(resultStr)
                    .createdAt(LocalDateTime.now())
                    .build();
            aiResultsMapper.insert(entity);
        } catch (Exception e) {
            // 保存失败不阻塞主流程
        }
    }

    // ==================== /predict/resource 资源需求智能预测 ====================

    @Override
    public Result<?> resourcePrediction(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String year = request.get("year") != null ? request.get("year").toString()
                : String.valueOf(Calendar.getInstance().get(Calendar.YEAR));
        String area = request.get("area") != null ? request.get("area").toString() : "全部";

        // 1. 构建查询条件
        QueryWrapper<PredictResource> qw = new QueryWrapper<>();
        qw.likeRight("stat_mouth", year);
        if (!"全部".equals(area)) {
            QueryWrapper<Region> regionQw = new QueryWrapper<>();
            regionQw.eq("name", area);
            Region region = regionMapper.selectOne(regionQw);
            if (region != null) {
                qw.eq("region_id", region.getId());
            }
        }
        qw.orderByAsc("stat_mouth");
        List<PredictResource> records = predictResourceMapper.selectList(qw);

        // 2. 按月聚合：waterDemand、fertilizerDemand、laborDemand
        BigDecimal[] waterMonthly = new BigDecimal[12];
        BigDecimal[] fertilizerMonthly = new BigDecimal[12];
        Integer[] laborMonthly = new Integer[12];
        Arrays.fill(waterMonthly, BigDecimal.ZERO);
        Arrays.fill(fertilizerMonthly, BigDecimal.ZERO);
        Arrays.fill(laborMonthly, 0);

        for (PredictResource r : records) {
            int monthIdx = extractMonthIndex(r.getStatMouth());
            if (monthIdx < 0 || monthIdx > 11) continue;
            waterMonthly[monthIdx] = waterMonthly[monthIdx].add(
                    r.getWaterDemand() != null ? r.getWaterDemand() : BigDecimal.ZERO);
            fertilizerMonthly[monthIdx] = fertilizerMonthly[monthIdx].add(
                    r.getFertilizerDemand() != null ? r.getFertilizerDemand() : BigDecimal.ZERO);
            laborMonthly[monthIdx] = laborMonthly[monthIdx] +
                    (r.getLaborDemand() != null ? r.getLaborDemand() : 0);
        }

        // 3. 确定已有数据最大月份
        int maxDataMonth = 0;
        for (int i = 11; i >= 0; i--) {
            if (waterMonthly[i].compareTo(BigDecimal.ZERO) > 0
                    || fertilizerMonthly[i].compareTo(BigDecimal.ZERO) > 0) {
                maxDataMonth = Math.max(maxDataMonth, i + 1);
                break;
            }
        }
        if (maxDataMonth == 0) {
            maxDataMonth = Calendar.getInstance().get(Calendar.MONTH) + 1;
        }

        int predictMonths = Math.min(3, Math.max(2, 12 - maxDataMonth));
        List<String> predictMonthLabels = new ArrayList<>();
        for (int i = 1; i <= predictMonths; i++) {
            int m = maxDataMonth + i;
            if (m > 12) break;
            predictMonthLabels.add(m + "月");
        }

        // 4. 调用 DeepSeek 预测后续月份
        Map<String, List<BigDecimal>> aiPredicted = new LinkedHashMap<>();
        try {
            aiPredicted = callDeepseekForResourcePredict(year, area,
                    waterMonthly, fertilizerMonthly, laborMonthly, maxDataMonth, predictMonthLabels);
        } catch (Exception e) {
            // 降级：用末月数据填充
            BigDecimal lastWater = getLastNonZero(waterMonthly, maxDataMonth);
            BigDecimal lastFert = getLastNonZero(fertilizerMonthly, maxDataMonth);
            int lastLabor = getLastNonZeroInt(laborMonthly, maxDataMonth);
            List<BigDecimal> predWater = new ArrayList<>();
            List<BigDecimal> predFert = new ArrayList<>();
            List<Integer> predLabor = new ArrayList<>();
            for (int i = 0; i < predictMonthLabels.size(); i++) {
                predWater.add(lastWater);
                predFert.add(lastFert);
                predLabor.add(lastLabor);
            }
            aiPredicted.put("water", predWater);
            aiPredicted.put("fertilizer", predFert);
            aiPredicted.put("labor", new ArrayList<>(predLabor.stream()
                    .map(v -> new BigDecimal(v)).collect(Collectors.toList())));
        }

        // 5. 拼接 AI 预测值到完整 12 个月
        List<BigDecimal> aiWater = aiPredicted.getOrDefault("water", new ArrayList<>());
        List<BigDecimal> aiFert = aiPredicted.getOrDefault("fertilizer", new ArrayList<>());
        List<BigDecimal> aiLabor = aiPredicted.getOrDefault("labor", new ArrayList<>());
        for (int i = 0; i < predictMonthLabels.size(); i++) {
            int target = maxDataMonth + i;
            if (target >= 12) break;
            if (i < aiWater.size()) waterMonthly[target] = aiWater.get(i);
            if (i < aiFert.size()) fertilizerMonthly[target] = aiFert.get(i);
            if (i < aiLabor.size()) laborMonthly[target] = aiLabor.get(i).intValue();
        }

        // 6. 拆分子类别
        // water → agriculture (87%) + ecology (13%)
        // fertilizer → seed.fertilizer
        // labor → 派生 seed.pesticide 和 seed.seed
        List<BigDecimal> agriculture = new ArrayList<>();
        List<BigDecimal> ecology = new ArrayList<>();
        List<BigDecimal> fertilizer = new ArrayList<>();
        List<BigDecimal> pesticide = new ArrayList<>();
        List<BigDecimal> seedList = new ArrayList<>();

        for (int i = 0; i < 12; i++) {
            BigDecimal w = waterMonthly[i].setScale(1, BigDecimal.ROUND_HALF_UP);
            agriculture.add(w.multiply(new BigDecimal("0.87")).setScale(1, BigDecimal.ROUND_HALF_UP));
            ecology.add(w.multiply(new BigDecimal("0.13")).setScale(1, BigDecimal.ROUND_HALF_UP));

            BigDecimal f = fertilizerMonthly[i].setScale(1, BigDecimal.ROUND_HALF_UP);
            fertilizer.add(f);

            // 农药 ≈ 化肥 × 0.03（比例系数）
            pesticide.add(f.multiply(new BigDecimal("0.03")).setScale(1, BigDecimal.ROUND_HALF_UP));
            // 种子需求 ≈ 劳动力 × 0.15
            seedList.add(new BigDecimal(laborMonthly[i])
                    .multiply(new BigDecimal("0.15")).setScale(1, BigDecimal.ROUND_HALF_UP));
        }

        // 7. 组装返回
        String[] monthLabels = {"1月","2月","3月","4月","5月","6月","7月","8月","9月","10月","11月","12月"};

        Map<String, Object> waterData = new LinkedHashMap<>();
        waterData.put("agriculture", agriculture);
        waterData.put("ecology", ecology);

        Map<String, Object> seedData = new LinkedHashMap<>();
        seedData.put("fertilizer", fertilizer);
        seedData.put("pesticide", pesticide);
        seedData.put("seed", seedList);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("months", Arrays.asList(monthLabels));
        data.put("areaOptions", List.of("全部", "北区", "南区", "东区", "西区"));
        data.put("yearOptions", List.of("2020", "2021", "2022", "2023", "2024", "2025", "2026"));
        data.put("water", waterData);
        data.put("seed", seedData);

        return Result.data(data);
    }

    private Map<String, List<BigDecimal>> callDeepseekForResourcePredict(
            String year, String area,
            BigDecimal[] waterMonthly, BigDecimal[] fertilizerMonthly, Integer[] laborMonthly,
            int maxDataMonth, List<String> predictMonthLabels) throws Exception {

        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            throw new IllegalStateException("DeepSeek API Key 未配置");
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是一个农业资源需求预测专家系统。\n");
        prompt.append("分析年份：").append(year).append("\n");
        prompt.append("片区范围：").append(area).append("\n\n");
        prompt.append("当前已有 ").append(maxDataMonth).append(" 个月的资源需求数据：\n");
        prompt.append("需水量(万吨): [");
        for (int i = 0; i < maxDataMonth; i++) {
            if (i > 0) prompt.append(", ");
            prompt.append(waterMonthly[i].stripTrailingZeros().toPlainString());
        }
        prompt.append("]\n");
        prompt.append("农资需求(吨): [");
        for (int i = 0; i < maxDataMonth; i++) {
            if (i > 0) prompt.append(", ");
            prompt.append(fertilizerMonthly[i].stripTrailingZeros().toPlainString());
        }
        prompt.append("]\n");
        prompt.append("劳动力需求(人): [");
        for (int i = 0; i < maxDataMonth; i++) {
            if (i > 0) prompt.append(", ");
            prompt.append(laborMonthly[i]);
        }
        prompt.append("]\n\n");
        prompt.append("请预测后续 ").append(predictMonthLabels.size()).append(" 个月的数据：");
        prompt.append(predictMonthLabels.toString()).append("\n");
        prompt.append("请输出严格的 JSON，字段包含：\n");
        prompt.append("water: 数组，预测的需水量，长度 ").append(predictMonthLabels.size()).append("\n");
        prompt.append("fertilizer: 数组，预测的农资需求，长度 ").append(predictMonthLabels.size()).append("\n");
        prompt.append("labor: 数组，预测的劳动力需求（整数），长度 ").append(predictMonthLabels.size()).append("\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");

        String baseUrl = System.getProperty("deepseek.base-url", "https://api.deepseek.com");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("temperature", 0.3);
        payload.put("messages", List.of(Map.of("role", "user", "content", prompt.toString())));

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(30))
                .build();
        HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("DeepSeek API 调用失败，HTTP 状态：" + response.statusCode());
        }

        Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
        if (choices == null || choices.isEmpty()) {
            throw new IllegalStateException("DeepSeek API 返回空结果");
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = Objects.toString(message.get("content"), "").trim();
        if (content.startsWith("```")) {
            content = content.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        }

        Map<String, Object> rawResult = objectMapper.readValue(content, Map.class);

        Map<String, List<BigDecimal>> result = new LinkedHashMap<>();
        for (String key : List.of("water", "fertilizer", "labor")) {
            List<?> values = (List<?>) rawResult.get(key);
            List<BigDecimal> decimalValues = new ArrayList<>();
            if (values != null) {
                for (Object v : values) {
                    decimalValues.add(new BigDecimal(v.toString()));
                }
            }
            result.put(key, decimalValues);
        }
        return result;
    }

    private BigDecimal getLastNonZero(BigDecimal[] arr, int maxMonth) {
        for (int i = maxMonth - 1; i >= 0; i--) {
            if (arr[i].compareTo(BigDecimal.ZERO) > 0) return arr[i];
        }
        return BigDecimal.ZERO;
    }

    private int getLastNonZeroInt(Integer[] arr, int maxMonth) {
        for (int i = maxMonth - 1; i >= 0; i--) {
            if (arr[i] != null && arr[i] > 0) return arr[i];
        }
        return 0;
    }

    @Override
    public Result<?> equipmentUpdate(Map<String, Object> request) {
        if (request == null || request.get("id") == null) {
            return Result.error("设备ID不能为空");
        }
        Long id = ((Number) request.get("id")).longValue();
        Equipment existing = equipmentMapper.selectById(id);
        if (existing == null) {
            return Result.error("设备不存在");
        }

        if (request.containsKey("name")) existing.setName(request.get("name").toString());
        if (request.containsKey("type")) existing.setType(request.get("type").toString());
        if (request.containsKey("eff")) existing.setEfficiency(request.get("eff").toString());
        if (request.containsKey("status")) existing.setStatus(request.get("status").toString());
        if (request.containsKey("score")) existing.setScore(((Number) request.get("score")).intValue());

        String areaName = request.get("area") != null ? request.get("area").toString() : null;
        if (areaName != null) {
            QueryWrapper<Region> regionQw = new QueryWrapper<>();
            regionQw.eq("name", areaName);
            Region region = regionMapper.selectOne(regionQw);
            if (region != null) {
                existing.setRegionId(region.getId());
            }
        }

        equipmentMapper.updateById(existing);
        return Result.success("更新成功");
    }

    @Override
    public Result<?> equipmentDelete(Long id) {
        if (id == null) {
            return Result.error("设备ID不能为空");
        }
        Equipment existing = equipmentMapper.selectById(id);
        if (existing == null) {
            return Result.error("设备不存在");
        }
        equipmentMapper.deleteById(id);
        return Result.success("删除成功");
    }
}