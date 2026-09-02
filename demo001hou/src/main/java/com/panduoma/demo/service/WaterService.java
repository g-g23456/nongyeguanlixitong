package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

public interface WaterService {

    /**
     * 分页查询水位配额列表
     */
    Result<?> waterQuota(int page, int size);
}
