package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.HistorySearchDto;
import com.heima.search.service.SearchBoxService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
@Api(tags = "搜索框相关接口")
public class SearchBoxController {

    @Autowired
    SearchBoxService searchBoxService;

    @PostMapping("/load")
    public ResponseResult loadHistorySearchRecords(){
        return ResponseResult.okResult(searchBoxService.loadHistorySearchRecords());
    }

    @PostMapping("/del")
    @ApiOperation("根据id删除搜索记录")
    public ResponseResult deleteHistorySearchRecord(@RequestBody HistorySearchDto dto){
        return ResponseResult.okResult(searchBoxService.deleteHistorySearchRecord(dto));
    }
}
