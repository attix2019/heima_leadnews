package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.wemedia.service.WmNewsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
@Api(tags = "自媒体作者文章相关接口")
public class NewsController {

    @Autowired
    WmNewsService wmNewsService;

    @PostMapping("/list")
    public ResponseResult getNewsList(@RequestBody WmNewsPageReqDto dto){
        return  wmNewsService.getNewsList(dto);
    }

    @PostMapping("/submit")
    @ApiOperation("提交文章")
    public ResponseResult submitNews(@RequestBody WmNewsDto wmNewsDto){
        return wmNewsService.submitNews(wmNewsDto);
    }

    @GetMapping("/one/{id}")
    @ApiOperation("查看文章详情")
    public ResponseResult getNewsDetail(@PathVariable Integer id){
        return wmNewsService.getNewsDetail(id);
    }

    @GetMapping("/del_news/{id}")
    @ApiOperation("查看文章详情")
    public ResponseResult deleteNews(@PathVariable Integer id){
        return wmNewsService.deleteNews(id);
    }

}
