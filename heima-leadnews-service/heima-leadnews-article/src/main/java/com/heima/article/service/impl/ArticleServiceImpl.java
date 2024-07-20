package com.heima.article.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleLoadingModeConstant;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService{

    @Autowired
    ArticleMapper articleMapper;

    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    @Override
    public ResponseResult loadArticlesByCriterias(ArticleHomeDto articleHomeDto, short type) {
        //1.校验参数
        Integer size = articleHomeDto.getSize();
        if(size == null || size == 0){
            size = 10;
        }
        size = Math.min(size,MAX_PAGE_SIZE);
        articleHomeDto.setSize(size);

        //类型参数检验
        if(!(ArticleLoadingModeConstant.LOAD_MORE).equals(type)&&
                !(ArticleLoadingModeConstant.LOAD_NEW).equals(type)){
            type = ArticleLoadingModeConstant.LOAD_MORE;
        }
        //文章频道校验
        if(StringUtils.isEmpty(articleHomeDto.getTag())){
            articleHomeDto.setTag(ArticleLoadingModeConstant.DEFAULT_CHANNEL);
        }

        //时间校验
        if(articleHomeDto.getMaxBehotTime() == null) {
            articleHomeDto.setMaxBehotTime(new Date());
        }
        if(articleHomeDto.getMinBehotTime() == null) {
            articleHomeDto.setMinBehotTime(new Date());
        }
        List<ApArticle> articles =articleMapper.getArticles(articleHomeDto, type);
        return ResponseResult.okResult(articles);
    }
}
