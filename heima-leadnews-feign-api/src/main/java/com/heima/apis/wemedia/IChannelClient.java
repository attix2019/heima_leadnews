package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(value = "leadnews-wemedia")
public interface IChannelClient {

    @PostMapping("/api/v1/channel/list")
    ResponseResult listChannels(ChannePageQuerylDto channePageQuerylDto);

    @PostMapping("/api/v1/channel/save")
    ResponseResult addChannel();
}
