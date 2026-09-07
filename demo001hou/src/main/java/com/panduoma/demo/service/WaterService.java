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

    /**
     * 水资源 AI 分析记录分页查询
     */
    Result<?> waterAIAnalysisList(int page, int size);

    /**
     * 用水数据分析
     */
    Result<?> waterAnalysis(int year);

    /**
     * 水位配额配置调整
     */
    Result<?> waterQuotaUpdate(Map<String, Object> request);

    /**
     * 删除水位配额
     */
    Result<?> waterQuotaDelete(Long id);
}