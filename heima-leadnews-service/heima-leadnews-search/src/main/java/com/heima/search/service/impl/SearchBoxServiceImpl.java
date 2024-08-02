package com.heima.search.service.impl;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.search.pojos.SearchWordRecord;
import com.heima.search.service.SearchBoxService;
import com.heima.search.util.SearchModuleThreadLocalUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class SearchBoxServiceImpl implements SearchBoxService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    @Async
    public void insertHistorySearchRecords(String keyword, Integer userId) {
        //1.查询当前用户的搜索关键词
        Query query = Query.query(Criteria.where("userId").is(userId).and("keyword").is(keyword));
        SearchWordRecord searchWordRecord = mongoTemplate.findOne(query, SearchWordRecord.class);

        //2.存在 更新创建时间
        if(searchWordRecord != null){
            searchWordRecord.setCreatedTime(new Date());
            mongoTemplate.save(searchWordRecord);
            return;
        }

        //3.不存在，判断当前历史记录总数量是否超过10
        searchWordRecord = new SearchWordRecord();
        searchWordRecord.setUserId(userId);
        searchWordRecord.setKeyword(keyword);
        searchWordRecord.setCreatedTime(new Date());

        Query query1 = Query.query(Criteria.where("userId").is(userId));
        query1.with(Sort.by(Sort.Direction.DESC,"createdTime"));
        List<SearchWordRecord> searchWordRecordList = mongoTemplate.find(query1, SearchWordRecord.class);

        if(searchWordRecordList == null || searchWordRecordList.size() < 10){
            mongoTemplate.save(searchWordRecord);
        }else {
            SearchWordRecord lastUserSearch = searchWordRecordList.get(searchWordRecordList.size() - 1);
            mongoTemplate.findAndReplace(Query.query(Criteria.where("id").is(lastUserSearch.getId())),searchWordRecord);
        }
    }

    @Override
    public ResponseResult loadHistorySearchRecords() {
        //获取当前用户
        ApUser user = SearchModuleThreadLocalUtils.getUser();
        if(user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //根据用户查询数据，按照时间倒序
        List<SearchWordRecord> searchWordRecordList = mongoTemplate.find(Query.query(Criteria.where("userId").is(user.getId())).with(Sort.by(Sort.Direction.DESC, "createdTime")), SearchWordRecord.class);
        return ResponseResult.okResult(searchWordRecordList);
    }
}
