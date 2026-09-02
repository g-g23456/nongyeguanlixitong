package com.panduoma.demo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.panduoma.demo.entity.WaterQuota;
import com.panduoma.demo.mapper.WaterQuotaMapper;
import com.panduoma.demo.response.Result;
import com.panduoma.demo.service.WaterService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class WaterServiceImpl implements WaterService {

    @Resource
    private WaterQuotaMapper waterQuotaMapper;

    @Override
    public Result<?> waterQuota(int page, int size) {
        page = Math.max(1, page);
        size = Math.max(1, size);

        Page<WaterQuota> resultPage = waterQuotaMapper.selectPage(new Page<>(page, size), new QueryWrapper<>());

        Map<String, Object> pageResult = new HashMap<>();
        pageResult.put("current", resultPage.getCurrent());
        pageResult.put("size", resultPage.getSize());
        pageResult.put("total", resultPage.getTotal());
        pageResult.put("records", resultPage.getRecords());
        return Result.data(pageResult);
    }
}
