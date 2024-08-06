package com.heima.admin.controller;

import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import com.heima.model.wemedia.dtos.ChannelDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ChannelManagementController {

    @Autowired
    IChannelClient channelClient;

    @PostMapping("/wemedia/api/v1/channel/list")
    public ResponseResult listChannels(@RequestBody  ChannePageQuerylDto dto){
        return channelClient.listChannels(dto);
    }

    @PostMapping("/wemedia/api/v1/channel/save")
    public ResponseResult addChannel(@RequestBody ChannelDto channelDto){
        log.info("ee");
        return channelClient.addChannel(channelDto);
    }
}
