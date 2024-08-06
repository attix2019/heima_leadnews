package com.heima.admin.controller;

import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelManagementController {

    @Autowired
    IChannelClient channelClient;

    @PostMapping("/wemedia/api/v1/channel/list")
    public ResponseResult listChannels(ChannePageQuerylDto dto){

        return channelClient.listChannels(dto);
    }
}
