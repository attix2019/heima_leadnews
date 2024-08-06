package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;

public interface ChannelService extends IService<WmChannel> {

    ResponseResult getChannelList();

    ResponseResult addChannel(ChannelDto channelDto);

    ResponseResult deleteChannel(Integer id);

    ResponseResult modifyChannel(ChannelDto channelDto);
}
