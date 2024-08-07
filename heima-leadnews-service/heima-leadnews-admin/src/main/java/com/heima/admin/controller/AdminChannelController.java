package com.heima.admin.controller;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import com.heima.model.wemedia.dtos.ChannelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
public class AdminChannelController {

    @Autowired
    IWemediaClient wemediaCient;

    @PostMapping("/wemedia/api/v1/channel/list")
    public ResponseResult listChannels(@RequestBody  ChannePageQuerylDto dto){
        return wemediaCient.listChannels(dto);
    }

    @PostMapping("/wemedia/api/v1/channel/save")
    public ResponseResult addChannel(@RequestBody ChannelDto channelDto){
        log.info("ee");
        return wemediaCient.addChannel(channelDto);
    }

    @GetMapping("/wemedia/api/v1/channel/del/{id}")
    public ResponseResult deleteChannel(@PathVariable Integer id){
        return wemediaCient.deleteChannel(id);
    }

    @PostMapping("/wemedia/api/v1/channel/update")
    public ResponseResult modifyChannel(@RequestBody ChannelDto channelDto){
        return wemediaCient.modifyChannel(channelDto);
    }

    @GetMapping("/wemedia/api/v1/news/one_vo/{id}")
    public ResponseResult getArticleDetail(@PathVariable Integer id){
        return wemediaCient.getArticleDetailForAdmin(id);
    }
}
