package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

import java.util.Map;

public interface SeedService {

    /**
     * 农资库存台账分页查询
     */
    Result<?> seedInventory(int page, int size);

    /**
     * 入库登记
     */
    Result<?> seedCreate(Map<String, Object> request);

    /**
     * AI 农资需求分配
     */
    Result<?> seedAllocation(Map<String, Object> request);

    /**
     * 消耗预测与库存预警
     */
    Result<?> seedPredict();

    /**
     * 更新农资
     */
    Result<?> seedUpdate(Map<String, Object> request);

    /**
     * 删除农资
     */
    Result<?> seedDelete(Long id);
}