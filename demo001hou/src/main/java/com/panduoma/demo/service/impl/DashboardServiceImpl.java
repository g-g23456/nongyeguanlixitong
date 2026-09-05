package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.panduoma.demo.entity.*;
import com.panduoma.demo.mapper.*;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.DashboardService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class DashboardServiceImpl implements DashboardService {

    @Resource
    private FarmlandTotalMapper farmlandTotalMapper;

    @Resource
    private WaterTotalMapper waterTotalMapper;

    @Resource
    private SeedInventoryMapper seedInventoryMapper;

    @Resource
    private EquipmentMapper equipmentMapper;

    @Resource
    private ResourceAlertMapper resourceAlertMapper;

    @Resource
    private ResourceOverviewMapper resourceOverviewMapper;

    @Resource
    private ResourceUtilTrendMapper resourceUtilTrendMapper;

    @Resource
    private FarmlandBlockMapper farmlandBlockMapper;

    @Resource
    private WaterQuotaMapper waterQuotaMapper;

    @Resource
    private RegionMapper regionMapper;

    @Override
    public Result<?> dashboardOverview() {
        Map<String, Object> data = new LinkedHashMap<>();

        // ========== stats ==========
        data.put("stats", buildStats());

        // ========== alerts ==========
        data.put("alerts", buildAlerts());

        // ========== chart1: resource_overview 五维柱状图 ==========
        data.put("chart1", buildChart1());

        // ========== chart2: farmland_blocks + water_quota 按片区 ==========
        data.put("chart2", buildChart2());

        // ========== chart3: resource_util_trend 四条折线 ==========
        data.put("chart3", buildChart3());

        return Result.data(data);
    }

    // ==================== stats ====================

    private Map<String, Object> buildStats() {
        Map<String, Object> stats = new LinkedHashMap<>();

        // 耕地总面积
        FarmlandTotal farmlandTotal = farmlandTotalMapper.selectOne(new QueryWrapper<FarmlandTotal>().last("LIMIT 1"));
        BigDecimal farmlandVal = farmlandTotal != null && farmlandTotal.getTotalQuota() != null
                ? farmlandTotal.getTotalQuota() : BigDecimal.ZERO;
        stats.put("farmland", farmlandVal + " 万亩");

        // 总水量
        WaterTotal waterTotal = waterTotalMapper.selectOne(new QueryWrapper<WaterTotal>().last("LIMIT 1"));
        BigDecimal waterVal = waterTotal != null && waterTotal.getTotalQuota() != null
                ? waterTotal.getTotalQuota() : BigDecimal.ZERO;
        stats.put("water", waterVal + " 万m³");

        // 总农资（inventory 求和）
        List<SeedInventory> seeds = seedInventoryMapper.selectList(new QueryWrapper<>());
        BigDecimal seedSum = seeds.stream()
                .map(s -> s.getInventory() != null ? s.getInventory() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        stats.put("seed", seedSum + " 万吨");

        // 总设备数
        Long equipmentCount = equipmentMapper.selectCount(new QueryWrapper<>());
        stats.put("equipment", equipmentCount + " 台");

        return stats;
    }

    // ==================== alerts ====================

    private List<Map<String, Object>> buildAlerts() {
        List<ResourceAlert> alerts = resourceAlertMapper.selectList(
                new QueryWrapper<ResourceAlert>().orderByDesc("create_time").last("LIMIT 20"));

        List<Map<String, Object>> result = new ArrayList<>();
        for (ResourceAlert alert : alerts) {
            Map<String, Object> item = new LinkedHashMap<>();

            // 根据 alert_level 映射 icon / bg / color
            String level = alert.getAlertLevel() != null ? alert.getAlertLevel() : "";
            switch (level) {
                case "danger":
                    item.put("icon", "warning");
                    item.put("bg", "#fff1f0");
                    break;
                case "warning":
                    item.put("icon", "info-circle");
                    item.put("bg", "#fffbe6");
                    break;
                case "success":
                    item.put("icon", "check-circle");
                    item.put("bg", "#f6ffed");
                    break;
                default:
                    item.put("icon", "info-circle");
                    item.put("bg", "#e6f7ff");
                    break;
            }

            item.put("text", alert.getAlertContent());
            result.add(item);
        }
        return result;
    }

    // ==================== chart1: resource_overview 五维柱状图 ====================

    private Map<String, Object> buildChart1() {
        List<ResourceOverview> records = resourceOverviewMapper.selectList(
                new QueryWrapper<ResourceOverview>().orderByAsc("stat_month"));

        List<String> xAxis = new ArrayList<>();
        List<Number> farmlandData = new ArrayList<>();
        List<Number> waterData = new ArrayList<>();
        List<Number> materialData = new ArrayList<>();
        List<Number> laborData = new ArrayList<>();
        List<Number> machineData = new ArrayList<>();

        for (ResourceOverview r : records) {
            xAxis.add(formatMonth(r.getStatMonth()));
            farmlandData.add(r.getFarmland() != null ? r.getFarmland() : 0);
            waterData.add(r.getWater() != null ? r.getWater() : 0);
            materialData.add(r.getMaterial() != null ? r.getMaterial() : 0);
            laborData.add(r.getLabor() != null ? r.getLabor() : 0);
            machineData.add(r.getMachine() != null ? r.getMachine() : 0);
        }

        List<Map<String, Object>> series = new ArrayList<>();
        series.add(barSeries("耕地", farmlandData, "#52c41a"));
        series.add(barSeries("用水", waterData, "#1890ff"));
        series.add(barSeries("农资", materialData, "#fa8c16"));
        series.add(barSeries("人力", laborData, "#722ed1"));
        series.add(barSeries("器械", machineData, "#eb2f96"));

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("xAxis", xAxis);
        chart.put("legend", List.of("耕地", "用水", "农资", "人力", "器械"));
        chart.put("series", series);
        return chart;
    }

    // ==================== chart2: farmland_blocks + water_quota 按片区 ====================

    private Map<String, Object> buildChart2() {
        // 查所有片区
        List<Region> regions = regionMapper.selectList(new QueryWrapper<>());
        Map<Long, String> regionNameMap = regions.stream()
                .collect(Collectors.toMap(Region::getId, Region::getName, (a, b) -> a));

        List<String> xAxis = new ArrayList<>(regionNameMap.values());

        // farmland_blocks 按 region_id 汇总 area
        List<FarmlandBlock> blocks = farmlandBlockMapper.selectList(new QueryWrapper<>());
        Map<Long, BigDecimal> farmlandByRegion = blocks.stream()
                .filter(b -> b.getRegionId() != null)
                .collect(Collectors.groupingBy(FarmlandBlock::getRegionId,
                        Collectors.reducing(BigDecimal.ZERO,
                                b -> b.getArea() != null ? b.getArea() : BigDecimal.ZERO,
                                BigDecimal::add)));

        // water_quota 按 region_id 汇总 quota
        List<WaterQuota> quotas = waterQuotaMapper.selectList(new QueryWrapper<>());
        Map<Long, BigDecimal> waterByRegion = quotas.stream()
                .filter(q -> q.getRegionId() != null)
                .collect(Collectors.groupingBy(WaterQuota::getRegionId,
                        Collectors.reducing(BigDecimal.ZERO,
                                q -> q.getQuota() != null ? q.getQuota() : BigDecimal.ZERO,
                                BigDecimal::add)));

        List<Number> farmlandData = new ArrayList<>();
        List<Number> waterData = new ArrayList<>();
        for (Long regionId : regionNameMap.keySet()) {
            farmlandData.add(farmlandByRegion.getOrDefault(regionId, BigDecimal.ZERO));
            waterData.add(waterByRegion.getOrDefault(regionId, BigDecimal.ZERO));
        }

        List<Map<String, Object>> series = new ArrayList<>();
        series.add(barSeries("耕地(亩)", farmlandData, "#52c41a"));

        // 用水用折线
        Map<String, Object> waterSeries = new LinkedHashMap<>();
        waterSeries.put("name", "用水(万m³)");
        waterSeries.put("type", "line");
        waterSeries.put("data", waterData);
        waterSeries.put("smooth", true);
        waterSeries.put("itemStyle", Map.of("color", "#1890ff"));
        series.add(waterSeries);

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("xAxis", xAxis);
        chart.put("legend", List.of("耕地(亩)", "用水(万m³)"));
        chart.put("series", series);
        return chart;
    }

    // ==================== chart3: resource_util_trend 四条折线 ====================

    private Map<String, Object> buildChart3() {
        List<ResourceUtilTrend> records = resourceUtilTrendMapper.selectList(
                new QueryWrapper<ResourceUtilTrend>().orderByAsc("stat_month"));

        List<String> xAxis = new ArrayList<>();
        List<Number> landRates = new ArrayList<>();
        List<Number> waterRates = new ArrayList<>();
        List<Number> materialRates = new ArrayList<>();
        List<Number> laborRates = new ArrayList<>();

        for (ResourceUtilTrend r : records) {
            xAxis.add(formatMonth(r.getStatMonth()));
            landRates.add(r.getLandUtilRate() != null ? r.getLandUtilRate() : 0);
            waterRates.add(r.getWaterUtilRate() != null ? r.getWaterUtilRate() : 0);
            materialRates.add(r.getMaterialUtilRate() != null ? r.getMaterialUtilRate() : 0);
            laborRates.add(r.getLaborUtilRate() != null ? r.getLaborUtilRate() : 0);
        }

        List<Map<String, Object>> series = new ArrayList<>();
        series.add(lineSeries("耕地利用率", landRates, "#52c41a"));
        series.add(lineSeries("水资源利用率", waterRates, "#1890ff"));
        series.add(lineSeries("农资利用率", materialRates, "#fa8c16"));
        series.add(lineSeries("人力利用率", laborRates, "#722ed1"));

        Map<String, Object> chart = new LinkedHashMap<>();
        chart.put("xAxis", xAxis);
        chart.put("legend", List.of("耕地利用率", "水资源利用率", "农资利用率", "人力利用率"));
        chart.put("series", series);
        return chart;
    }

    // ==================== 工具方法 ====================

    private Map<String, Object> barSeries(String name, List<Number> data, String color) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("name", name);
        s.put("type", "bar");
        s.put("data", data);
        s.put("itemStyle", Map.of("color", color));
        return s;
    }

    private Map<String, Object> lineSeries(String name, List<Number> data, String color) {
        Map<String, Object> s = new LinkedHashMap<>();
        s.put("name", name);
        s.put("type", "line");
        s.put("data", data);
        s.put("smooth", true);
        s.put("itemStyle", Map.of("color", color));
        return s;
    }

    /**
     * 将 "YYYY-MM" 转为 "X月" 格式
     */
    private String formatMonth(String statMonth) {
        if (statMonth == null || statMonth.length() < 7) return statMonth;
        try {
            int month = Integer.parseInt(statMonth.substring(5, 7));
            return month + "月";
        } catch (Exception e) {
            return statMonth;
        }
    }
}
