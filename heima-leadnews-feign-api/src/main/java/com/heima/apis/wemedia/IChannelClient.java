package com.heima.apis.wemedia;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import com.heima.model.wemedia.dtos.ChannelDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(value = "leadnews-wemedia")
public interface IChannelClient {

    @PostMapping("/api/v1/channel/list")
    ResponseResult listChannels(@RequestBody ChannePageQuerylDto channePageQuerylDto);

    @PostMapping("/api/v1/channel/save")
    ResponseResult addChannel(@RequestBody ChannelDto channelDto);

    @GetMapping("/api/v1/channel/del/{id}")
    ResponseResult deleteChannel(@PathVariable Integer id);
}
