package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;

public interface SearchBoxService {

    void insertHistorySearchRecords(String keyword,Integer userId);

    ResponseResult loadHistorySearchRecords();

    ResponseResult deleteHistorySearchRecord(HistorySearchDto dto);
}
