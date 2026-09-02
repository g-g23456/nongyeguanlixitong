package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panduoma.demo.entity.Famlandoptimize;
import com.panduoma.demo.entity.FarmlandBlock;
import com.panduoma.demo.entity.Farmlandrotation;
import com.panduoma.demo.mapper.FarmlandBlockMapper;
import com.panduoma.demo.mapper.FarmlandOptimizeResultMapper;
import com.panduoma.demo.mapper.FarmlandRotationMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.FarmlandService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;

@Service
public class FarmlandServiceImpl implements FarmlandService {

    @Resource
    private FarmlandBlockMapper farmlandBlockMapper;

    @Resource
    private FarmlandOptimizeResultMapper farmlandOptimizeResultMapper;

    @Resource
    private FarmlandRotationMapper farmlandRotationMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Resource
    private DataSource dataSource;

    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;

    @Override
    public Result<?> farmlandList(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        Page<FarmlandBlock> resultPage = farmlandBlockMapper.selectPage(new Page<>(page, size), new QueryWrapper<>());

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("current", resultPage.getCurrent());
        pageResult.put("size", resultPage.getSize());
        pageResult.put("total", resultPage.getTotal());
        pageResult.put("records", resultPage.getRecords());
        return Result.data(pageResult);
    }

    @Override
    public Result<?> farmlandCreate(FarmlandBlock farmlandBlock) {
        if (farmlandBlock == null) {
            return Result.error("请求参数不能为空");
        }
        if (!StringUtils.hasText(farmlandBlock.getBlockCode())) {
            return Result.error("地块编码不能为空");
        }
        if (!StringUtils.hasText(farmlandBlock.getBlockName())) {
            return Result.error("地块名称不能为空");
        }

        LocalDateTime now = LocalDateTime.now();
        farmlandBlock.setCreatedAt(now);
        farmlandBlock.setUpdatedAt(now);

        farmlandBlockMapper.insert(farmlandBlock);
        return Result.data(farmlandBlock);
    }

    @Override
    public Result<?> farmlandOptimize(Map<String, Object> request) {
        if (request == null || request.isEmpty()) {
            return Result.error("请求参数不能为空");
        }

        try {
            String taskId = String.valueOf(request.getOrDefault("taskId", UUID.randomUUID().toString().replace("-", "")));
            List<Map<String, Object>> rotationContext = fetchRotationContext();
            String prompt = buildDeepseekPrompt(request, rotationContext);
            Map<String, Object> deepseekResult = callDeepseek(prompt);

            Map<String, Object> payloadToSave = new LinkedHashMap<>();
            payloadToSave.put("taskId", taskId);
            payloadToSave.put("question", request);
            payloadToSave.put("rotationContext", rotationContext);
            payloadToSave.put("prompt", prompt);

            Famlandoptimize optimize = Famlandoptimize.builder()
                    .taskId(taskId)
                    .requestPayload(objectMapper.writeValueAsString(payloadToSave))
                    .result(objectMapper.writeValueAsString(deepseekResult))
                    .createdAt(LocalDateTime.now())
                    .build();

            farmlandOptimizeResultMapper.insert(optimize);

            Map<String, Object> response = new HashMap<>();
            response.put("id", optimize.getId());
            response.put("taskId", optimize.getTaskId());
            response.putAll(deepseekResult);
            return Result.data(response);
        } catch (Exception e) {
            return Result.error("AI 优化执行失败：" + e.getMessage());
        }
    }

    @Override
    public Result<?> farmlandRotation() {
        List<Farmlandrotation> records = farmlandRotationMapper.selectList(
                new QueryWrapper<Farmlandrotation>().orderByAsc("year")
        );

        Set<Integer> yearSet = new TreeSet<>();
        Map<Integer, Map<String, Double>> yearCropMap = new LinkedHashMap<>();
        Set<String> cropNameSet = new TreeSet<>();

        for (Farmlandrotation record : records) {
            if (record.getYear() == null || !StringUtils.hasText(record.getPlan())) {
                continue;
            }
            int year = record.getYear();
            yearSet.add(year);
            yearCropMap.computeIfAbsent(year, k -> new LinkedHashMap<>());

            try {
                Object parsed = objectMapper.readValue(record.getPlan(), Object.class);
                Map<String, Double> cropMap = yearCropMap.get(year);

                if (parsed instanceof Map) {
                    @SuppressWarnings("unchecked")
                    Map<String, Object> planMap = (Map<String, Object>) parsed;
                    for (Map.Entry<String, Object> entry : planMap.entrySet()) {
                        String cropName = entry.getKey();
                        double value = ((Number) entry.getValue()).doubleValue();
                        cropMap.merge(cropName, value, Double::sum);
                        cropNameSet.add(cropName);
                    }
                } else if (parsed instanceof List) {
                    @SuppressWarnings("unchecked")
                    List<Map<String, Object>> planList = (List<Map<String, Object>>) parsed;
                    for (Map<String, Object> item : planList) {
                        String cropName = Objects.toString(item.get("name"), "");
                        if (cropName.isEmpty()) continue;
                        double value = 0;
                        if (item.containsKey("value")) {
                            value = ((Number) item.get("value")).doubleValue();
                        } else if (item.containsKey("area")) {
                            value = ((Number) item.get("area")).doubleValue();
                        }
                        Map<String, Double> cm = yearCropMap.get(year);
                        cm.merge(cropName, value, Double::sum);
                        cropNameSet.add(cropName);
                    }
                }
            } catch (Exception ignored) {
            }
        }

        List<String> years = yearSet.stream().map(String::valueOf).toList();

        List<Map<String, Object>> series = new ArrayList<>();
        for (String cropName : cropNameSet) {
            List<Double> data = new ArrayList<>();
            for (Integer year : yearSet) {
                Map<String, Double> cropMap = yearCropMap.get(year);
                data.add(cropMap != null ? cropMap.getOrDefault(cropName, 0.0) : 0.0);
            }
            Map<String, Object> s = new LinkedHashMap<>();
            s.put("name", cropName);
            s.put("data", data);
            series.add(s);
        }

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("years", years);
        result.put("series", series);
        return Result.data(result);
    }

    // ==================== 私有辅助方法 ====================

    private List<Map<String, Object>> fetchRotationContext() {
        List<Map<String, Object>> rows = new ArrayList<>();
        String sql = "SELECT * FROM farmland_rotation ORDER BY id DESC LIMIT 20";

        try (Connection connection = dataSource.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql);
             ResultSet resultSet = statement.executeQuery()) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            while (resultSet.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= metaData.getColumnCount(); i++) {
                    String columnName = metaData.getColumnLabel(i);
                    row.put(columnName, resultSet.getObject(i));
                }
                rows.add(row);
            }
        } catch (SQLException ignored) {
            return rows;
        }
        return rows;
    }

    private String buildDeepseekPrompt(Map<String, Object> request, List<Map<String, Object>> rotationContext) throws JsonProcessingException {
        Map<String, Object> constraints = request.containsKey("constraints") && request.get("constraints") instanceof Map
                ? (Map<String, Object>) request.get("constraints")
                : new HashMap<>();

        StringBuilder prompt = new StringBuilder();
        prompt.append("你是农业种植优化专家。请根据当前地区、地块属性和轮作规律，给出最佳作物配置方案。\n");
        prompt.append("用户需求：\n");
        prompt.append("年份：").append(request.getOrDefault("year", "当前年")).append("\n");
        prompt.append("优化目标：").append(request.getOrDefault("optimizationGoal", "maximizeYield")).append("\n");
        prompt.append("目标描述：").append(request.getOrDefault("objectiveLabel", "产量最大化")).append("\n");
        prompt.append("约束条件：").append(objectMapper.writeValueAsString(constraints)).append("\n");

        if (rotationContext != null && !rotationContext.isEmpty()) {
            prompt.append("参考的地区与土地轮作历史数据：\n");
            prompt.append(objectMapper.writeValueAsString(rotationContext.subList(0, Math.min(rotationContext.size(), 10)))).append("\n");
        } else {
            prompt.append("参考数据：无 farmland_rotation 表数据，按常规农作物适宜性和区域种植规则进行推断。\n");
        }

        prompt.append("请输出严格的 JSON，字段必须包含：cropDistribution、blockSuitability、optimizationMetrics。\n");
        prompt.append("cropDistribution 是数组，每个对象包含 name 和 percentage，百分比总和需约为 100。\n");
        prompt.append("blockSuitability 是数组，每个对象包含 blockId、blockCode、recommendedCrop、suitabilityScore。\n");
        prompt.append("optimizationMetrics 是对象，包含 objectiveConvergence 和 constraintSatisfactionRate。\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");
        return prompt.toString();
    }

    private Map<String, Object> callDeepseek(String prompt) throws Exception {
        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            return buildFallbackResult();
        }

        String baseUrl = System.getProperty("deepseek.base-url", "https://api.deepseek.com");
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("model", "deepseek-chat");
        payload.put("temperature", 0.2);
        payload.put("messages", List.of(Map.of("role", "user", "content", prompt)));

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(baseUrl + "/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(payload)))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() >= 400) {
            throw new IllegalStateException("DeepSeek API 调用失败，HTTP 状态：" + response.statusCode() + "，响应：" + response.body());
        }

        Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
        if (choices == null || choices.isEmpty()) {
            return buildFallbackResult();
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = Objects.toString(message.get("content"), "");
        String cleanJson = content.trim();
        if (cleanJson.startsWith("```")) {
            cleanJson = cleanJson.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        }

        Map<String, Object> result = objectMapper.readValue(cleanJson, Map.class);
        return normalizeOptimizationResult(result);
    }

    private Map<String, Object> buildFallbackResult() {
        Map<String, Object> result = new LinkedHashMap<>();

        List<Map<String, Object>> cropDistribution = new ArrayList<>();
        cropDistribution.add(Map.of("name", "水稻", "percentage", 38.5));
        cropDistribution.add(Map.of("name", "小麦", "percentage", 27.0));
        cropDistribution.add(Map.of("name", "玉米", "percentage", 24.5));
        cropDistribution.add(Map.of("name", "大豆", "percentage", 10.0));

        List<Map<String, Object>> blockSuitability = new ArrayList<>();
        blockSuitability.add(Map.of(
                "blockId", 101,
                "blockCode", "F001",
                "recommendedCrop", "水稻",
                "suitabilityScore", 92
        ));

        Map<String, Object> metrics = new LinkedHashMap<>();
        metrics.put("objectiveConvergence", 0.987);
        metrics.put("constraintSatisfactionRate", 0.96);

        result.put("cropDistribution", cropDistribution);
        result.put("blockSuitability", blockSuitability);
        result.put("optimizationMetrics", metrics);
        return result;
    }

    private Map<String, Object> normalizeOptimizationResult(Map<String, Object> result) {
        Map<String, Object> normalized = new LinkedHashMap<>();
        normalized.put("cropDistribution", result.getOrDefault("cropDistribution", buildFallbackResult().get("cropDistribution")));
        normalized.put("blockSuitability", result.getOrDefault("blockSuitability", buildFallbackResult().get("blockSuitability")));

        Map<String, Object> metrics = result.get("optimizationMetrics") instanceof Map
                ? (Map<String, Object>) result.get("optimizationMetrics")
                : new LinkedHashMap<>();
        Map<String, Object> safeMetrics = new LinkedHashMap<>();
        safeMetrics.put("objectiveConvergence", metrics.getOrDefault("objectiveConvergence", 0.95));
        safeMetrics.put("constraintSatisfactionRate", metrics.getOrDefault("constraintSatisfactionRate", 0.95));
        normalized.put("optimizationMetrics", safeMetrics);
        return normalized;
    }
}
