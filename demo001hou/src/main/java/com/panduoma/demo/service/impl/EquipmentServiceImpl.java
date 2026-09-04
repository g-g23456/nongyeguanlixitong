package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panduoma.demo.entity.Equipment;
import com.panduoma.demo.entity.EquipmentMaintenance;
import com.panduoma.demo.entity.Region;
import com.panduoma.demo.mapper.EquipmentMapper;
import com.panduoma.demo.mapper.EquipmentMaintenanceMapper;
import com.panduoma.demo.mapper.RegionMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.EquipmentService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
    public Result<?> equipmentMaintenance() {
        List<EquipmentMaintenance> records = equipmentMaintenanceMapper.selectList(
                new QueryWrapper<EquipmentMaintenance>().orderByAsc("stat_month"));

        // 汇总统计
        BigDecimal totalCostSum = BigDecimal.ZERO;
        int totalPlanCount = 0;
        int totalManageCount = 0;
        int totalProceedCount = 0;
        int totalOverdueCount = 0;

        List<Map<String, Object>> monthlyData = new ArrayList<>();
        for (EquipmentMaintenance r : records) {
            BigDecimal cost = r.getTotalCost() != null ? r.getTotalCost() : BigDecimal.ZERO;
            int plan = r.getPlanCount() != null ? r.getPlanCount() : 0;
            int manage = r.getManageCount() != null ? r.getManageCount() : 0;
            int proceed = r.getProceedCount() != null ? r.getProceedCount() : 0;
            int overdue = r.getOverdueCount() != null ? r.getOverdueCount() : 0;
            totalCostSum = totalCostSum.add(cost);
            totalPlanCount += plan;
            totalManageCount += manage;
            totalProceedCount += proceed;
            totalOverdueCount += overdue;

            Map<String, Object> item = new LinkedHashMap<>();
            item.put("month", r.getStatMonth());
            item.put("totalCost", cost);
            item.put("planCount", plan);
            item.put("manageCount", manage);
            item.put("proceedCount", proceed);
            item.put("overdueCount", overdue);
            monthlyData.add(item);
        }

        Map<String, Object> summary = new LinkedHashMap<>();
        summary.put("totalCost", totalCostSum);
        summary.put("planCount", totalPlanCount);
        summary.put("manageCount", totalManageCount);
        summary.put("proceedCount", totalProceedCount);
        summary.put("overdueCount", totalOverdueCount);

        Map<String, Object> data = new LinkedHashMap<>();
        data.put("summary", summary);
        data.put("monthlyData", monthlyData);
        return Result.data(data);
    }
}
