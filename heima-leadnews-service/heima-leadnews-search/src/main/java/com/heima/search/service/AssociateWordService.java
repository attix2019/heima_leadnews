package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.ArticleSearchDto;

public interface AssociateWordService {

    ResponseResult findAssociateWords(ArticleSearchDto articleSearchDto);
}
