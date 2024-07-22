package com.heima.article.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;

public interface ArticleService extends IService<ApArticle> {

    ResponseResult loadArticlesByCriterias(ArticleHomeDto articleHomeDto, short type);

    ResponseResult saveArticle(ArticleDto articleDto);
}
