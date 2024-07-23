package com.heima.wemedia.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.article.IArticleClient;
import com.heima.common.constants.WemediaConstants;
import com.heima.common.exception.CustomException;
import com.heima.common.tess4j.Tess4jClient;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.*;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.wemedia.mapper.*;
import com.heima.wemedia.service.WmNewsService;
import com.heima.wemedia.util.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.TesseractException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@Slf4j
public class WmNewsServiceImpl extends ServiceImpl<WmNewsMapper, WmNews> implements WmNewsService {

    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;

    @Autowired
    WmNewsMapper wmNewsMapper;

    @Autowired
    WmMaterialMapper wmMaterialMapper;

    @Autowired
    ChannelMapper channelMapper;

    @Autowired
    WmUserMapper wmUserMapper;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private WmSensitiveMapper wmSensitiveMapper;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    Tess4jClient tess4jClient;

    @Override
    public ResponseResult getNewsList(WmNewsPageReqDto dto) {
        //1.检查参数
        dto.checkParam();
        //2.分页查询
        IPage page = new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmNews> lambdaQueryWrapper = new LambdaQueryWrapper<>();


        // 状态，起始结束时间， 用户，频道，关键字
        if(dto.getStatus() != null) {
            lambdaQueryWrapper.eq(WmNews::getStatus, dto.getStatus());
        }
        if(dto.getBeginPubDate() != null && dto.getEndPubDate() != null){
            lambdaQueryWrapper.ge( WmNews::getCreatedTime,dto.getBeginPubDate());
            lambdaQueryWrapper.le( WmNews::getCreatedTime,dto.getEndPubDate());
        }
        lambdaQueryWrapper.eq(WmNews::getUserId, WmThreadLocalUtils.getUser().getId());
        if(dto.getChannelId() != null) {
            lambdaQueryWrapper.eq(WmNews::getChannelId, dto.getChannelId());
        }
        if(StringUtils.isNotBlank(dto.getKeyword())){
            lambdaQueryWrapper.like(StringUtils.isNotBlank(dto.getKeyword()), WmNews::getTitle, dto.getKeyword());
        }

        //按照时间倒序
        lambdaQueryWrapper.orderByDesc(WmNews::getCreatedTime);

        page = page(page,lambdaQueryWrapper);
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }


    public void checkParamsBeforeSubmit(WmNewsDto wmNewsDto){
        if(wmNewsDto == null || wmNewsDto.getContent() == null){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
    }

    // 检查userid和newsId是否匹配
    public void checkNewsUserIdMatchesOperatorId(WmNewsDto wmNewsDto){
        WmNews correspondingNews = getOne(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getId ,wmNewsDto.getId()));
        if(!correspondingNews.getUserId().equals( WmThreadLocalUtils.getUser().getId())){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
    }

    public WmNews generateNewsFromDto(WmNewsDto wmNewsDto){
        WmNews wmNews = new WmNews();
        BeanUtils.copyProperties(wmNewsDto, wmNews);
        wmNews.setUserId(WmThreadLocalUtils.getUser().getId());
        wmNews.setCreatedTime(new Date());
        wmNews.setSubmitedTime(new Date());
        wmNews.setEnable((short)1);//默认上架
        if(wmNewsDto.getImages() != null && wmNewsDto.getImages().size() > 0){
            String imageStr = StringUtils.join(wmNewsDto.getImages(), ",");
            wmNews.setImages(imageStr);
        }
        //如果当前封面类型为自动 -1
        if(wmNewsDto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            wmNews.setType(null);
        }
        return wmNews;
    }

    public void saveMaterialsAndNewsRelations(WmNewsDto wmNewsDto, Integer id){
        //保存文章内容图片与素材的关系
        List<String> materials = extractMaterialUrl(wmNewsDto.getContent());
        saveRelativeInfoForContent(materials, id);
        //保存文章封面图片与素材的关系，如果当前布局是自动，需要匹配封面图片
        // dto中images字段本身就指封面图片，所以无需像内容图片那样提取
        saveRelativeInfoForCover(wmNewsDto,id,materials);
    }

    @Override
    @Transactional
    // todo 加上分布式事务
    public ResponseResult submitNews(WmNewsDto wmNewsDto) {
        checkParamsBeforeSubmit(wmNewsDto);
        if(wmNewsDto.getId() != null){
            checkNewsUserIdMatchesOperatorId(wmNewsDto);
            wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId,
                    wmNewsDto.getId()));
        }
        WmNews wmNews = generateNewsFromDto(wmNewsDto);
        Map<String, Object> textAndImages = extractTextAndImages(wmNews);
        censorTextLocally((String) textAndImages.get("content"), wmNews);
        censorImageLocally((List<String>) textAndImages.get("images"),wmNews);

        saveOrUpdate(wmNews);
        if(wmNewsDto.getStatus() == 1){
            mockCensorContent(wmNews);
        }
        saveMaterialsAndNewsRelations(wmNewsDto, wmNews.getId());
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 1。从自媒体文章的内容中提取文本和图片
     * 2.提取文章的封面图片
     */
    private Map<String, Object> extractTextAndImages(WmNews wmNews) {
        StringBuilder stringBuilder = new StringBuilder();
        List<String> images = new ArrayList<>();
        //1。从自媒体文章的内容中提取文本和图片
        stringBuilder.append(wmNews.getTitle());
        if (StringUtils.isNotBlank(wmNews.getContent())) {
            List<Map> maps = JSONArray.parseArray(wmNews.getContent(), Map.class);
            for (Map map : maps) {
                if (map.get("type").equals("text")) {
                    stringBuilder.append(map.get("value"));
                }
                if (map.get("type").equals("image")) {
                    images.add((String) map.get("value"));
                }
            }
        }
        //2.提取文章的封面图片
        if (StringUtils.isNotBlank(wmNews.getImages())) {
            String[] split = wmNews.getImages().split(",");
            images.addAll(Arrays.asList(split));
        }
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("content", stringBuilder.toString());
        resultMap.put("images", images);
        return resultMap;
    }

    private void censorImageLocally(List<String> images, WmNews wmNews){
        if (images == null || images.size() == 0) {
            return;
        }
        images = images.stream().distinct().collect(Collectors.toList());
        for (String image : images) {
            byte[] bytes = fileStorageService.downLoadFile(image);
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            BufferedImage imageFile = null;
            try {
                imageFile = ImageIO.read(in);
            } catch (IOException e) {
                e.printStackTrace();
            }
            String result = null;
            try {
                result = tess4jClient.doOCR(imageFile);
            } catch (TesseractException e) {
                e.printStackTrace();
            }
            censorTextLocally(result, wmNews);
        }
    }

    private void censorTextLocally(String content, WmNews wmNews) {
        //获取所有的敏感词
        List<WmSensitiveWord> wmSensitives = wmSensitiveMapper.selectList
                (Wrappers.<WmSensitiveWord>lambdaQuery().select(WmSensitiveWord::getSensitives));
        List<String> sensitiveList = wmSensitives.stream().map(WmSensitiveWord::getSensitives).collect(Collectors.toList());

        //初始化敏感词库
        SensitiveWordUtil.initMap(sensitiveList);

        //查看文章中是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if(map.size() >0){
            wmNews.setStatus(WmNews.Status.FAIL.getCode());
            wmNews.setReason("当前文章中存在违规内容"+map);
            updateById(wmNews);
            throw new CustomException(AppHttpCodeEnum.CENSOR_FAILED);
        }
    }

    // todo 待接入真正的内容审查接口
    // 暂时的做法是，标题里含有“[设置审核状态:\d]”这样的文本，就将文章的状态设置为对应的值
    @Async
    void mockCensorContent(WmNews wmNews){
        if(!wmNews.getStatus().equals(WmNews.Status.SUBMIT.getCode())){
            return;
        }
        // 模拟审核
        String title = wmNews.getTitle();
        String pattern = "\\[设置审核状态:(\\d)\\]";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(title);
        // 如果不包含关键字，默认就设置为审核通过
        Short status = WmNews.Status.PUBLISHED.getCode();
        if (m.find()) {
            status = Short.valueOf(m.group(1));
        }
        log.info("当前文章的审核结果为: " + status);
        wmNews.setStatus(status);
        if(status == WmNews.Status.PUBLISHED.getCode()) {
            wmNews.setReason("审核成功");
            wmNews.setPublishTime(new Date());
            //4.审核成功，调用用户article服务接口
            ResponseResult responseResult = saveAppArticle(wmNews);
            if (!responseResult.getCode().equals(200)) {
                throw new RuntimeException("WmNewsAutoScanServiceImpl-文章审核，保存app端相关文章数据失败");
            }
            //回填article_id
            wmNews.setArticleId((Long) responseResult.getData());
        }
        wmNewsMapper.updateById(wmNews);
    }

    /**
     * 调用article服务接口在app端保存文章
     */
    private ResponseResult saveAppArticle(WmNews wmNews) {

        ArticleDto dto = new ArticleDto();
        BeanUtils.copyProperties(wmNews,dto);
        dto.setLayout(wmNews.getType());
        WmChannel wmChannel = channelMapper.selectById(wmNews.getChannelId());
        if(wmChannel != null){
            dto.setChannelName(wmChannel.getName());
        }
        dto.setAuthorId(wmNews.getUserId().longValue());
        WmUser wmUser = wmUserMapper.selectById(wmNews.getUserId());
        if(wmUser != null){
            dto.setAuthorName(wmUser.getName());
        }
        if(wmNews.getArticleId() != null){
            dto.setId(wmNews.getArticleId());
        }
        dto.setCreatedTime(new Date());

        ResponseResult responseResult = articleClient.saveArticle(dto);
        return responseResult;
    }

    @Override
    public ResponseResult getNewsDetail(Integer id) {
        WmNews wmNews = getOne(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getId, id));
        if(wmNews == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        return ResponseResult.okResult(wmNews);
    }

    @Override
    @Transactional
    public ResponseResult deleteNews(Integer id) {
        if(id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章id不可缺少");
        }
        WmNews wmNews = getOne(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getId, id));
        if(wmNews == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "文章不存在");
        }
        if(wmNews.getStatus().equals(WmNews.Status.PUBLISHED)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "文章已发布，不能删除");
        }
        wmNewsMapper.deleteById(wmNews.getId());
        // delete from wm_news_material where news_id = ?
        wmNewsMaterialMapper.delete(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getNewsId, id));
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


    /**
     * 处理文章内容图片与素材的关系
     */
    private void saveRelativeInfoForContent(List<String> materials, Integer newsId) {
        saveRelativeInfo(materials,newsId,WemediaConstants.WM_CONTENT_REFERENCE);
    }

    /**
     * 第一个功能：如果当前封面类型为自动，则设置封面类型的数据
     * 匹配规则：
     * 1，如果内容图片大于等于1，小于3  单图  type 1
     * 2，如果内容图片大于等于3  多图  type 3
     * 3，如果内容没有图片，无图  type 0
     *
     * 第二个功能：保存封面图片与素材的关系
     * @param dto
     * @param id
     * @param materials
     */
    private void saveRelativeInfoForCover(WmNewsDto dto, Integer id, List<String> materials) {
        List<String> images = dto.getImages();
        //如果当前封面类型为自动，则设置封面类型的数据
        if(dto.getType().equals(WemediaConstants.WM_NEWS_TYPE_AUTO)){
            //多图
            WmNews tmp = new WmNews();
            tmp.setId(id);
            if(materials.size() >= 3){
                tmp.setType(WemediaConstants.WM_NEWS_MANY_IMAGE);
                images = materials.stream().limit(3).collect(Collectors.toList());
            }else if(materials.size() >= 1 && materials.size() < 3){
                //单图
                tmp.setType(WemediaConstants.WM_NEWS_SINGLE_IMAGE);
                images = materials.stream().limit(1).collect(Collectors.toList());
            }else {
                //无图
                tmp.setType(WemediaConstants.WM_NEWS_NONE_IMAGE);
            }
            // 封面设置为自动类型的话需要额外更新一次
            tmp.setImages(StringUtils.join(images, ","));
            updateById(tmp);
        }
        if(images != null && images.size() > 0){
            saveRelativeInfo(images,id,WemediaConstants.WM_COVER_REFERENCE);
        }
    }

    /**
     * 保存 素材（来自内容或来自封面）与文章 的关系到数据库中
     * @param type 表示素材是封面引用还是内容引用
     */
    private void saveRelativeInfo(List<String> materials, Integer newsId, Short type) {
        if(materials!=null && !materials.isEmpty()){
            //通过图片的url查询素材的id
            List<WmMaterial> dbMaterials = wmMaterialMapper.selectList(Wrappers.<WmMaterial>lambdaQuery().in(WmMaterial::getUrl, materials));

            //判断素材是否有效
            if(dbMaterials==null || dbMaterials.size() == 0 || materials.size() != dbMaterials.size()){
                throw new CustomException(AppHttpCodeEnum.MATERIASL_REFERENCE_FAIL);
            }
            List<Integer> idList = dbMaterials.stream().map(WmMaterial::getId).collect(Collectors.toList());
            wmNewsMaterialMapper.saveRelations(idList,newsId,type);
        }

    }

    /**
     * 提取文章内容中的素材url
     */
    private List<String> extractMaterialUrl(String content) {
        List<String> materials = new ArrayList<>();
        List<Map> maps = JSON.parseArray(content, Map.class);
        for (Map map : maps) {
            if(map.get("type").equals("image")){
                String imgUrl = (String) map.get("value");
                materials.add(imgUrl);
            }
        }
        return materials;
    }


}
