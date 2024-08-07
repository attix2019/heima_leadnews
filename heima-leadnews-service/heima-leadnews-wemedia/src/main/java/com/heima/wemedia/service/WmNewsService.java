package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.admin.dtos.AdminArticlePageDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

import javax.xml.ws.Response;

public interface WmNewsService extends IService<WmNews> {

    ResponseResult getNewsList(WmNewsPageReqDto dto);

    ResponseResult submitNews(WmNewsDto wmNewsDto);

    ResponseResult getNewsDetail(Integer id);

    ResponseResult deleteNews(Integer id);

    ResponseResult listOrDelistNews(WmNewsDto dto);

    // 为admin提供的接口实现
    ResponseResult listArticlesForAdmin(AdminArticlePageDto dto);

    ResponseResult getArticleDetailForAdmin(Integer id);
}
