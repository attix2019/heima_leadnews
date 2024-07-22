package com.heima.article.feign;

import com.heima.apis.article.IArticleClient;
import com.heima.article.service.ArticleService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "用户端文章相关远程调用接口")
@RestController
public class ArticleClient implements IArticleClient {

    @Autowired
    ArticleService articleService;

    @Override
    @ApiOperation("保存用户端文章")
    public ResponseResult saveArticle(@RequestBody ArticleDto articleDto) {
        return articleService.saveArticle(articleDto);
    }
}
