package com.heima.search.service;

import com.heima.model.search.dtos.ArticleSearchDto;
import com.heima.model.common.dtos.ResponseResult;

import java.io.IOException;

public interface ArticleSearchService {

    /**
     ES文章分页搜索
     @return
     */
    ResponseResult search(ArticleSearchDto userSearchDto) throws IOException;
}