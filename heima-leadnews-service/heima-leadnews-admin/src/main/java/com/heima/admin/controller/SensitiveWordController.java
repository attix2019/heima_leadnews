package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.SensitiveWordDto;
import com.heima.model.wemedia.dtos.SensitiveWordPageQueryDto;
import com.heima.model.wemedia.pojos.WmSensitiveWord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class SensitiveWordController {

    @Autowired
    IWemediaClient wemediaClient;

    @PostMapping("/wemedia/api/v1/sensitive/list")
    ResponseResult listSensitiveWords(@RequestBody SensitiveWordPageQueryDto dto){
        return wemediaClient.listSensitiveWords(dto);
    }

    @DeleteMapping("/wemedia/api/v1/sensitive/del/{id}")
    ResponseResult deleteSensitiveWord(@PathVariable Integer id){
        return wemediaClient.deleteSensitiveWord(id);
    }

    @PostMapping("/wemedia/api/v1/sensitive/save")
    ResponseResult addSensitiveWord(@RequestBody SensitiveWordDto sensitiveWordDto){
        return wemediaClient.addSensitiveWord(sensitiveWordDto);
    }

    @PostMapping("/wemedia/api/v1/sensitive/update")
    ResponseResult modifySensitiveWord(@RequestBody SensitiveWordDto sensitiveWordDto){
        return wemediaClient.modifySensitiveWord(sensitiveWordDto);
    }

}

