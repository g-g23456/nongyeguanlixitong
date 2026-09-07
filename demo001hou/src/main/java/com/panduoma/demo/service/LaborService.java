package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

import java.util.Map;

public interface LaborService {

    /**
     * 劳动力列表分页查询
     */
    Result<?> laborList(int page, int size);

    /**
     * 劳动力排班
     */
    Result<?> laborOldSchedule(Map<String, Object> request);

    /**
     * 智能排班调度（AI）
     */
    Result<?> laborSchedule(Map<String, Object> request);

    /**
     * 新增劳动力
     */
    Result<?> laborCreate(Map<String, Object> request);

    /**
     * 更新劳动力
     */
    Result<?> laborUpdate(Map<String, Object> request);

    /**
     * 删除劳动力
     */
    Result<?> laborDelete(String laborId);
}