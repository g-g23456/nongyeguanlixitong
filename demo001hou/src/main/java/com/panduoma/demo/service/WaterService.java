package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

import java.util.Map;

public interface WaterService {

    /**
     * 分页查询水位配额列表
     */
    Result<?> waterQuota(int page, int size);

    /**
     * 当前水分配状态
     */
    Result<?> waterStatus();

    /**
     * AI 水位分配
     */
    Result<?> waterAllocation(Map<String, Object> request);
}
