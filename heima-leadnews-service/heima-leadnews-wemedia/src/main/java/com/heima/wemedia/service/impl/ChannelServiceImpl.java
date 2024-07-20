package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.ChannelMapper;
import com.heima.wemedia.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChannelServiceImpl extends ServiceImpl<ChannelMapper, WmChannel> implements ChannelService {

    @Autowired
    ChannelMapper channelMapper;

    @Override
    public ResponseResult getChannelList() {
        List<WmChannel> channelList = channelMapper.selectList(null);
        return ResponseResult.okResult(channelList);
    }
}
