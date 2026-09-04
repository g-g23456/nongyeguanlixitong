package com.panduoma.demo.service;

import com.panduoma.demo.response.Result;

public interface LaborService {

    /**
     * 劳动力列表分页查询
     */
    Result<?> laborList(int page, int size);
}
