package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.search.service.SearchBoxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/history")
public class SearchBoxController {

    @Autowired
    SearchBoxService searchBoxService;

    @PostMapping("/load")
    public ResponseResult loadHistorySearchRecords(){
        return ResponseResult.okResult(searchBoxService.loadHistorySearchRecords());
    }
}
