package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

import java.util.Map;

public interface EquipmentService {

    /**
     * 农机具台账分页查询
     */
    Result<?> equipmentList(int page, int size);

    /**
     * 新增设备
     */
    Result<?> equipmentCreate(Map<String, Object> request);

    /**
     * 维护保养管理
     */
    Result<?> equipmentMaintenance();

    /**
     * 设备调配分布（饼图+柱状图）
     */
    Result<?> equipmentStatus();

    /**
     * 智能调配与路径优化（DeepSeek AI）
     */
    Result<?> machineryDispatchCreate(Map<String, Object> request);

    /**
     * AI 综合决策中心
     */
    Result<?> aiDecision(Map<String, Object> request);

    /**
     * 作物产量预测
     */
    Result<?> yieldPrediction(Map<String, Object> request);

    /**
     * 资源需求智能预测
     */
    Result<?> resourcePrediction(Map<String, Object> request);

    /**
     * 更新设备
     */
    Result<?> equipmentUpdate(Map<String, Object> request);

    /**
     * 删除设备
     */
    Result<?> equipmentDelete(Long id);
}