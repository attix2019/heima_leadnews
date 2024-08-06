package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.mapper.ChannelMapper;
import com.heima.wemedia.service.ChannelService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Override
    public ResponseResult addChannel(ChannelDto channelDto) {
        WmChannel wmChannel = channelMapper.selectOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName,
                channelDto.getName()));
        if(wmChannel != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "同名频道已经存在");
        }
        WmChannel tmp = new WmChannel();
        BeanUtils.copyProperties(channelDto, tmp);
        tmp.setCreatedTime(new Date());
        tmp.setIsDefault(true);
        save(tmp);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
