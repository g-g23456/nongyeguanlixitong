package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panduoma.demo.entity.LaborWorkers;
import com.panduoma.demo.entity.Region;
import com.panduoma.demo.mapper.LaborWorkersMapper;
import com.panduoma.demo.mapper.RegionMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.LaborService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class LaborServiceImpl implements LaborService {

    @Resource
    private LaborWorkersMapper laborWorkersMapper;

    @Resource
    private RegionMapper regionMapper;

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
}
