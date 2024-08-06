package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.SensitiveWordPageQueryDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SensitiveWordController {

    @Autowired
    IWemediaClient wemediaClient;

    @PostMapping("/wemedia/api/v1/sensitive/list")
    ResponseResult listSensitiveWords(@RequestBody SensitiveWordPageQueryDto dto){
        return wemediaClient.listSensitiveWords(dto);
    }
}
