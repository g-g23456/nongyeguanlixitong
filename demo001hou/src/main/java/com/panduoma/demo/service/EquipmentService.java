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
}
