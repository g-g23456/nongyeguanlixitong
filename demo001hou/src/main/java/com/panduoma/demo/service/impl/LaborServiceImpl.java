package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.panduoma.demo.entity.FarmlandBlock;
import com.panduoma.demo.entity.LaborSchedule;
import com.panduoma.demo.entity.LaborWorkers;
import com.panduoma.demo.entity.Region;
import com.panduoma.demo.mapper.FarmlandBlockMapper;
import com.panduoma.demo.mapper.LaborScheduleMapper;
import com.panduoma.demo.mapper.LaborWorkersMapper;
import com.panduoma.demo.mapper.RegionMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.LaborService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaborServiceImpl implements LaborService {

    @Resource
    private LaborWorkersMapper laborWorkersMapper;

    @Resource
    private LaborScheduleMapper laborScheduleMapper;

    @Resource
    private RegionMapper regionMapper;

    @Resource
    private FarmlandBlockMapper farmlandBlockMapper;

    @Resource
    private ObjectMapper objectMapper;

    @Value("${deepseek.api-key:}")
    private String deepseekApiKey;

    @Override
    public Result<?> laborList(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        Page<LaborWorkers> resultPage = laborWorkersMapper.selectPage(
                new Page<>(page, size),
                new QueryWrapper<LaborWorkers>().orderByAsc("labor_id"));

        long total = resultPage.getTotal();
        List<LaborWorkers> records = resultPage.getRecords();

        List<Region> allRegions = regionMapper.selectList(new QueryWrapper<>());
        Map<Long, String> regionNameMap = allRegions.stream()
                .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));

        List<Map<String, Object>> items = new ArrayList<>();
        for (LaborWorkers record : records) {
            Map<String, Object> item = new LinkedHashMap<>();
            item.put("id", record.getLaborId());
            item.put("name", record.getLaborName());
            item.put("type", record.getWorkType());
            item.put("level", record.getSkillLevel());
            item.put("area", regionNameMap.getOrDefault(record.getRegionId(), ""));
            item.put("salary", record.getDailySalary());
            item.put("available", record.getAvailableTime());
            item.put("status", record.getWorkStatus());
            items.add(item);
        }

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("total", total);
        data.put("items", items);
        return Result.data(data);
    }

    @Override
    public Result<?> laborOldSchedule(Map<String, Object> request) {
        // 查询 labor_schedule 表全部记录
        List<LaborSchedule> schedules = laborScheduleMapper.selectList(
                new QueryWrapper<LaborSchedule>().orderByDesc("id"));

        // 汇总计算
        int totalDemand = 0;
        int totalSupply = 0;
        int totalGap = 0;

        List<Map<String, Object>> chartData = new ArrayList<>();
        for (LaborSchedule s : schedules) {
            int demand = s.getDemandNum() != null ? s.getDemandNum() : 0;
            int supply = s.getSupplyNum() != null ? s.getSupplyNum() : 0;
            int gap = s.getGapNum() != null ? s.getGapNum() : 0;
            totalDemand += demand;
            totalSupply += supply;
            totalGap += gap;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("task", s.getTaskName());
            item.put("demand", demand);
            item.put("supply", supply);
            item.put("gap", gap);
            chartData.add(item);
        }

        // 匹配率 = 总供给 / 总需求 * 100
        double matchRate = totalDemand > 0
                ? Math.round(totalSupply * 1000.0 / totalDemand) / 10.0
                : 100.0;
        // 缺口占比 = 总缺口 / 总需求 * 100
        double gapPercent = totalDemand > 0
                ? Math.round(totalGap * 1000.0 / totalDemand) / 10.0
                : 0.0;

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("matchRate", matchRate);
        data.put("gapPercent", gapPercent);
        data.put("chartData", chartData);
        return Result.data(data);
    }

    @Override
    public Result<?> laborSchedule(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String task = Objects.toString(request.get("task"), "春耕播种");
        String area = Objects.toString(request.get("area"), "全部片区");

        try {
            // 查询所有相关数据
            List<LaborWorkers> workers = laborWorkersMapper.selectList(
                    new QueryWrapper<LaborWorkers>().eq("work_status", "在岗"));
            List<LaborSchedule> schedules = laborScheduleMapper.selectList(new QueryWrapper<>());
            List<FarmlandBlock> farmlands = farmlandBlockMapper.selectList(new QueryWrapper<>());
            List<Region> regions = regionMapper.selectList(new QueryWrapper<>());

            Map<Long, String> regionNameMap = regions.stream()
                    .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));

            // 构建 prompt
            String prompt = buildLaborSchedulePrompt(task, area, workers, schedules, farmlands, regionNameMap);
            Map<String, Object> deepseekResult = callDeepseekForSchedule(prompt);

            // 提取 chartData 和 scheduleDetails
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> chartData = (List<Map<String, Object>>) deepseekResult.getOrDefault("chartData", List.of());
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> scheduleDetails = (List<Map<String, Object>>) deepseekResult.getOrDefault("scheduleDetails", List.of());

            // 计算 matchRate 和 gapPercent
            int totalDemand = 0;
            int totalSupply = 0;
            int totalGap = 0;
            for (Map<String, Object> item : chartData) {
                int demand = item.get("demand") != null ? ((Number) item.get("demand")).intValue() : 0;
                int supply = item.get("supply") != null ? ((Number) item.get("supply")).intValue() : 0;
                int gap = item.get("gap") != null ? ((Number) item.get("gap")).intValue() : 0;
                totalDemand += demand;
                totalSupply += supply;
                totalGap += gap;
            }

            double matchRate = totalDemand > 0
                    ? Math.round(totalSupply * 1000.0 / totalDemand) / 10.0
                    : 100.0;
            double gapPercent = totalDemand > 0
                    ? Math.round(totalGap * 1000.0 / totalDemand) / 10.0
                    : 0.0;

            Map<String, Object> data = new LinkedHashMap<>();
            data.put("matchRate", matchRate);
            data.put("gapPercent", gapPercent);
            data.put("taskName", task);
            data.put("areaName", area);
            data.put("chartData", chartData);
            data.put("scheduleDetails", scheduleDetails);
            return Result.data(data);
        } catch (Exception e) {
            return Result.error("智能排班调度失败：" + e.getMessage());
        }
    }

    private String buildLaborSchedulePrompt(String task, String area, List<LaborWorkers> workers,
                                            List<LaborSchedule> schedules, List<FarmlandBlock> farmlands,
                                            Map<Long, String> regionNameMap) throws Exception {
        StringBuilder prompt = new StringBuilder();
        prompt.append("你是农业劳动力智能调度专家。请根据当前劳动力资源、农田数据和历史排班记录，给出最优的智能排班方案。\n");
        prompt.append("当前任务：").append(task).append("\n");
        prompt.append("片区范围：").append(area).append("\n\n");

        prompt.append("当前在岗劳动力数据：\n");
        prompt.append(objectMapper.writeValueAsString(workers.stream().map(w -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("labor_id", w.getLaborId());
            m.put("labor_name", w.getLaborName());
            m.put("work_type", w.getWorkType());
            m.put("skill_level", w.getSkillLevel());
            m.put("region", regionNameMap.getOrDefault(w.getRegionId(), "未知"));
            m.put("available_time", w.getAvailableTime());
            return m;
        }).collect(Collectors.toList()))).append("\n\n");

        prompt.append("农田地块数据：\n");
        prompt.append(objectMapper.writeValueAsString(farmlands.stream().map(f -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("name", f.getBlockName());
            m.put("region", regionNameMap.getOrDefault(f.getRegionId(), "未知"));
            m.put("area", f.getArea());
            m.put("suitable_crops", f.getSuitableCrops());
            m.put("current_crop", f.getCurrentCrop());
            return m;
        }).collect(Collectors.toList()))).append("\n\n");

        prompt.append("历史排班记录：\n");
        prompt.append(objectMapper.writeValueAsString(schedules)).append("\n\n");

        prompt.append("请输出严格的 JSON，字段必须包含：\n");
        prompt.append("chartData: 数组，包含4个农时任务的劳动力需求/供给/缺口数据，每个对象包含 task(任务名)、demand(需求人数)、supply(供给人数)、gap(缺口人数)。数值要合理，与劳动力总数匹配。\n");
        prompt.append("scheduleDetails: 数组，为当前任务分配具体劳动力，每个对象包含 workerId(劳动力编号)、workerName(姓名)、task(任务名)、area(片区名)、shift(时段)。从在岗劳动力中选取，根据片区和技能匹配。如果片区为'全部片区'则从所有片区选人。\n");
        prompt.append("不要输出 Markdown 代码块，只输出纯 JSON。\n");
        return prompt.toString();
    }

    private Map<String, Object> callDeepseekForSchedule(String prompt) throws Exception {
        String apiKey = StringUtils.hasText(deepseekApiKey)
                ? deepseekApiKey
                : System.getenv().getOrDefault("DEEPSEEK_API_KEY", "");
        if (!StringUtils.hasText(apiKey)) {
            return buildFallbackSchedule();
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
            throw new IllegalStateException("DeepSeek API 调用失败，HTTP 状态：" + response.statusCode());
        }

        Map<String, Object> root = objectMapper.readValue(response.body(), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) root.get("choices");
        if (choices == null || choices.isEmpty()) {
            return buildFallbackSchedule();
        }

        Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
        String content = Objects.toString(message.get("content"), "").trim();
        if (content.startsWith("```")) {
            content = content.replaceFirst("^```(?:json)?\\s*", "").replaceFirst("\\s*```$", "");
        }

        Map<String, Object> result = objectMapper.readValue(content, Map.class);
        return result;
    }

    private Map<String, Object> buildFallbackSchedule() {
        Map<String, Object> result = new LinkedHashMap<>();
        List<Map<String, Object>> chartData = new ArrayList<>();
        chartData.add(Map.of("task", "春耕播种", "demand", 1200, "supply", 1100, "gap", 100));
        chartData.add(Map.of("task", "夏种管理", "demand", 980, "supply", 920, "gap", 60));
        chartData.add(Map.of("task", "秋收作业", "demand", 1500, "supply", 1450, "gap", 50));
        chartData.add(Map.of("task", "冬藏整地", "demand", 600, "supply", 580, "gap", 20));
        result.put("chartData", chartData);

        List<Map<String, Object>> scheduleDetails = new ArrayList<>();
        scheduleDetails.add(Map.of("workerId", "L001", "workerName", "张师傅", "task", "春耕播种", "area", "东片区", "shift", "全天"));
        scheduleDetails.add(Map.of("workerId", "L002", "workerName", "李师傅", "task", "春耕播种", "area", "西片区", "shift", "白天"));
        result.put("scheduleDetails", scheduleDetails);
        return result;
    }

    @Override
    public Result<?> laborCreate(Map<String, Object> request) {
        if (request == null) {
            return Result.error("请求参数不能为空");
        }
        String laborId = request.get("id") != null ? request.get("id").toString() : null;
        String laborName = request.get("name") != null ? request.get("name").toString() : null;
        String workType = request.get("type") != null ? request.get("type").toString() : null;
        String skillLevel = request.get("level") != null ? request.get("level").toString() : null;
        String areaName = request.get("area") != null ? request.get("area").toString() : null;
        BigDecimal dailySalary = request.get("salary") != null ? new BigDecimal(request.get("salary").toString()) : BigDecimal.ZERO;
        String availableTime = request.get("available") != null ? request.get("available").toString() : null;
        String workStatus = request.get("status") != null ? request.get("status").toString() : null;

        // 校验编号是否已存在
        if (laborId != null) {
            QueryWrapper<LaborWorkers> qw = new QueryWrapper<>();
            qw.eq("labor_id", laborId);
            Long count = laborWorkersMapper.selectCount(qw);
            if (count > 0) {
                return Result.error(400, "劳动力编号已存在");
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

        // 构建实体并写入
        LaborWorkers entity = LaborWorkers.builder()
                .laborId(laborId)
                .laborName(laborName)
                .workType(workType)
                .skillLevel(skillLevel)
                .regionId(regionId)
                .dailySalary(dailySalary)
                .availableTime(availableTime)
                .workStatus(workStatus)
                .build();
        laborWorkersMapper.insert(entity);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("id", laborId);
        data.put("name", laborName);
        data.put("type", workType);
        data.put("level", skillLevel);
        data.put("area", areaName);
        data.put("salary", dailySalary);
        data.put("available", availableTime);
        data.put("status", workStatus);
        return Result.data("新增劳动力成功", data);
    }
}
