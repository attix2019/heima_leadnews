package com.heima.wemedia.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.dtos.SensitiveWordPageQueryDto;
import com.heima.model.wemedia.pojos.WmSensitiveWord;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.ChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    ChannelService channelService;

    @Autowired
    WmSensitiveMapper wmSensitiveMapper;

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

    @Override
    public ResponseResult listSensitiveWords(@RequestBody SensitiveWordPageQueryDto dto) {
        QueryWrapper<WmSensitiveWord> wrapper = new QueryWrapper<>();
        if(StringUtils.isNotBlank(dto.getKeyword())){
            wrapper.like("sensitives", "%" +  dto.getKeyword() + "%");
        }
        List<WmSensitiveWord> sensitiveWordList = wmSensitiveMapper.selectList(wrapper);
        return ResponseResult.okResult(sensitiveWordList);
    }
}
