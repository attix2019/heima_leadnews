package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IChannelClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.wemedia.service.ChannelService;
import org.simpleframework.xml.Path;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChannelClient implements IChannelClient {

    @Autowired
    ChannelService channelService;

    @Override
    public ResponseResult listChannels(ChannePageQuerylDto channePageQuerylDto) {
        return channelService.getChannelList();
    }

    @Override
    public ResponseResult addChannel(ChannelDto channelDto) {
        return channelService.addChannel(channelDto);
    }

    @Override
    @GetMapping("/api/v1/channel/del/{id}")
    public ResponseResult deleteChannel( @PathVariable  Integer id) {
        return channelService.deleteChannel(id);
    }

    @Override
    public ResponseResult modifyChannel(@RequestBody ChannelDto channelDto) {
        return channelService.modifyChannel(channelDto);
    }
}
