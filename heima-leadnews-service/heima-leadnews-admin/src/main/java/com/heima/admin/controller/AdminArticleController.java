package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.dtos.AdminArticlePageDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AdminArticleController {

    @Autowired
    IWemediaClient wemediaClient;

    @PostMapping("/wemedia/api/v1/news/list_vo")
    public ResponseResult listArticles(@RequestBody AdminArticlePageDto dto){
        return wemediaClient.listArticles(dto);
    }
}
