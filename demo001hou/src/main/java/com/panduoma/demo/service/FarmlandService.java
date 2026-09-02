package com.panduoma.demo.service;

import com.panduoma.demo.entity.FarmlandBlock;
import com.panduoma.demo.response.Result;

import java.util.Map;

public interface FarmlandService {

    /**
     * 分页查询地块台账列表
     */
    Result<?> farmlandList(int page, int size);

    /**
     * 创建耕地地块
     */
    Result<?> farmlandCreate(FarmlandBlock farmlandBlock);

    /**
     * AI 优化农田布局
     */
    Result<?> farmlandOptimize(Map<String, Object> request);

    /**
     * 获取地块轮作历史数据
     */
    Result<?> farmlandRotation();
}
