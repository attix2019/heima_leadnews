package com.heima.article.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService{

    @Autowired
    ArticleMapper articleMapper;

    @Override
    public ResponseResult loadArticlesByCriterias(ArticleHomeDto articleHomeDto, short type) {
        List<ApArticle> articles =articleMapper.getArticles(articleHomeDto, type);
        return ResponseResult.okResult(articles);
    }
}
