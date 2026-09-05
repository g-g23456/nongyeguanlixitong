package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

public interface DashboardService {

    /**
     * 获取看板总览数据
     */
    Result<?> dashboardOverview();
}
