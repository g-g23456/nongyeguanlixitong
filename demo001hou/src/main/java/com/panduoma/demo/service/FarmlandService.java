package com.panduoma.demo.service;

import com.panduoma.demo.entity.FarmlandBlock;
import com.panduoma.demo.entity.FarmlandOptimizeRequest;
import com.panduoma.demo.response.Result;

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
    Result<?> farmlandOptimize(FarmlandOptimizeRequest request);

    /**
     * 获取地块轮作历史数据
     */
    Result<?> farmlandRotation(Integer year);

    /**
     * 更新耕地地块
     */
    Result<?> farmlandUpdate(FarmlandBlock farmlandBlock);

    /**
     * 删除耕地地块
     */
    Result<?> farmlandDelete(Long id);

    /**
     * 获取最新的优化结果
     */
    Result<?> getLatestOptimize();
}