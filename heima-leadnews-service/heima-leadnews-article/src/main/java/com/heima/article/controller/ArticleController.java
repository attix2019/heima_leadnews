package com.heima.article.controller;

import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleConstants;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.common.dtos.ResponseResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/article")
@Slf4j
public class ArticleController {

    @Autowired
    ArticleService articleService;

    @PostMapping("/load")
    public ResponseResult loadArticle(@RequestBody ArticleHomeDto articleHomeDto){
        return articleService.loadArticlesByCriterias(articleHomeDto,
                ArticleConstants.LOAD_MORE);
    }

    @PostMapping("/loadmore")
    public ResponseResult loadMoreArticle(@RequestBody ArticleHomeDto articleHomeDto){
        return articleService.loadArticlesByCriterias(articleHomeDto,
                ArticleConstants.LOAD_MORE);
    }

    @PostMapping("/loadnew")
    public ResponseResult loadEarlierArticle(@RequestBody ArticleHomeDto articleHomeDto){
        return articleService.loadArticlesByCriterias(articleHomeDto,
                ArticleConstants.LOAD_NEW);
    }
}
