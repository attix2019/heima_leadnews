package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;

public interface SearchBoxService {

    void insertHistorySearchRecords(String keyword,Integer userId);

    ResponseResult loadHistorySearchRecords();
}
