package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.util.WmThreadLocalUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;

@Service
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {


    @Override
    public ResponseResult getNewsList(WmNewsPageReqDto dto) {
        //1.检查参数
        dto.checkParam();
        //2.分页查询
        IPage page = new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();


        // 状态，起始结束时间， 用户，频道，关键字
        if(dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        if(dto.getBeginPubDate() != null && dto.getEndPubDate() != null){
            lambdaQueryWrapper.ge( WmNews::getCreatedTime,dto.getBeginPubDate());
            lambdaQueryWrapper.le( WmNews::getCreatedTime,dto.getEndPubDate());
        }
        lambdaQueryWrapper.eq(WmNews::getUserId, WmThreadLocalUtils.getUser().getId());
        if(dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }
        if(StringUtils.isNotBlank(dto.getKeyword())){
            lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getKeyword()), WmNews::getTitle, dto.getKeyword());
        }

        //按照时间倒序
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        page = page(page,lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }
}
