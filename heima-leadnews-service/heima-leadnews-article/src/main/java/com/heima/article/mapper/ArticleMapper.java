package com.heima.article.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author chengr25
 */
@Mapper
public interface ArticleMapper extends BaseMapper<ApArticle> {

    List<ApArticle> getArticles(ArticleHomeDto articleHomeDto, short type);
}
