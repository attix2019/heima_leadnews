package com.heima.wemedia.feign;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.model.admin.dtos.AdminArticlePageDto;
import com.heima.model.admin.dtos.ReviewOpinion;
import com.heima.model.admin.vos.AdminArticleListItemVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.ChannePageQuerylDto;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.dtos.SensitiveWordDto;
import com.heima.model.wemedia.dtos.SensitiveWordPageQueryDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitiveWord;
import com.heima.wemedia.mapper.WmNewsMapper;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.ChannelService;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    ChannelService channelService;

    @Autowired
    WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    WmNewsService wmNewsService;

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

    @Override
    @DeleteMapping("/api/v1/sensitive/del/{id}")
    public ResponseResult deleteSensitiveWord(@PathVariable Integer id) {
        WmSensitiveWord word = wmSensitiveMapper.selectById(id);
        if(word == null){
            return  ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "敏感词不存在");
        }
        wmSensitiveMapper.deleteById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private Boolean theWordAlreadyExists(String wordText){
        WmSensitiveWord sensitiveWord = wmSensitiveMapper.selectOne(Wrappers.<WmSensitiveWord>lambdaQuery().eq(
                WmSensitiveWord::getSensitives, wordText));
        return (sensitiveWord != null);

    }

    @Override
    public ResponseResult addSensitiveWord(@RequestBody SensitiveWordDto sensitiveWordDto) {
        if(theWordAlreadyExists(sensitiveWordDto.getSensitives())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "敏感词已经存在");
        }
        WmSensitiveWord wmSensitiveWord = new WmSensitiveWord();
        BeanUtils.copyProperties(sensitiveWordDto, wmSensitiveWord);
        wmSensitiveWord.setCreatedTime(new Date());
        wmSensitiveMapper.insert(wmSensitiveWord);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult modifySensitiveWord(@RequestBody SensitiveWordDto sensitiveWordDto) {
        if(theWordAlreadyExists(sensitiveWordDto.getSensitives())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "敏感词已经存在");
        }
        WmSensitiveWord wmSensitiveWord = new WmSensitiveWord();
        BeanUtils.copyProperties(sensitiveWordDto, wmSensitiveWord);
        wmSensitiveMapper.updateById(wmSensitiveWord);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult listArticles(@RequestBody  AdminArticlePageDto dto) {
        return wmNewsService.listArticlesForAdmin(dto);
    }

    @Override
    @GetMapping("/api/v1/news/one_vo/{id}")
    public ResponseResult getArticleDetailForAdmin(@PathVariable Integer id) {
        return wmNewsService.getArticleDetailForAdmin(id);
    }

    @Override
    public ResponseResult passReview(@RequestBody ReviewOpinion reviewOpinion) {
        wmNewsService.passManualReview(reviewOpinion);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult rejectNewsViaManualReview(@RequestBody ReviewOpinion reviewOpinion) {
        wmNewsService.rejectNewsViaManualReview(reviewOpinion);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
