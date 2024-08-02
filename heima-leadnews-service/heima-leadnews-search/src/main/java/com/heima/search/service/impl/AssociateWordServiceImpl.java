package com.heima.search.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.search.dtos.ArticleSearchDto;
import com.heima.search.pojos.AssociateWord;
import com.heima.search.service.AssociateWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AssociateWordServiceImpl implements AssociateWordService {

    @Autowired
    MongoTemplate mongoTemplate;

    @Override
    public ResponseResult findAssociateWords(ArticleSearchDto articleSearchDto) {
        //1 参数检查
        if(articleSearchDto == null || StringUtils.isBlank(articleSearchDto.getSearchWords())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //分页检查
        if (articleSearchDto.getPageSize() > 20) {
            articleSearchDto.setPageSize(20);
        }

        //3 执行查询 模糊查询
        Query query = Query.query(Criteria.where("associateWords").regex(".*?\\" + articleSearchDto.getSearchWords() + ".*"));
        query.limit(articleSearchDto.getPageSize());
        List<AssociateWord> wordsList = mongoTemplate.find(query, AssociateWord.class);

        return ResponseResult.okResult(wordsList);
    }
}
