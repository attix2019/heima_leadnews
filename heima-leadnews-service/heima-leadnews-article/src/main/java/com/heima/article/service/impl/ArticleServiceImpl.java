package com.heima.article.service.impl;

import com.alibaba.cloud.commons.lang.StringUtils;
import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.article.mapper.ArticleConfigMapper;
import com.heima.article.mapper.ArticleContentMapper;
import com.heima.article.mapper.ArticleMapper;
import com.heima.article.service.ArticleService;
import com.heima.common.constants.ArticleLoadingModeConstant;
import com.heima.common.exception.CustomException;
import com.heima.file.service.FileStorageService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import freemarker.template.Configuration;
import freemarker.template.Template;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class ArticleServiceImpl extends ServiceImpl<ArticleMapper, ApArticle> implements ArticleService{

    @Autowired
    ArticleMapper articleMapper;

    @Autowired
    ArticleConfigMapper articleConfigMapper;

    @Autowired
    ArticleContentMapper articleContentMapper;

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    Configuration configuration;

    @Autowired
    ArticleServiceImpl proxy;

    // 单页最大加载的数字
    private final static short MAX_PAGE_SIZE = 50;

    @Override
    public ResponseResult loadArticlesByCriterias(ArticleHomeDto articleHomeDto, short type) {
        //1.校验参数
        Integer size = articleHomeDto.getSize();
        if(size == null || size == 0){
            size = 10;
        }
        size = Math.min(size,MAX_PAGE_SIZE);
        articleHomeDto.setSize(size);

        //类型参数检验
        if(!(ArticleLoadingModeConstant.LOAD_MORE).equals(type)&&
                !(ArticleLoadingModeConstant.LOAD_NEW).equals(type)){
            type = ArticleLoadingModeConstant.LOAD_MORE;
        }
        //文章频道校验
        if(StringUtils.isEmpty(articleHomeDto.getTag())){
            articleHomeDto.setTag(ArticleLoadingModeConstant.DEFAULT_CHANNEL);
        }

        //时间校验
        if(articleHomeDto.getMaxBehotTime() == null) {
            articleHomeDto.setMaxBehotTime(new Date());
        }
        if(articleHomeDto.getMinBehotTime() == null) {
            articleHomeDto.setMinBehotTime(new Date());
        }
        List<ApArticle> articles =articleMapper.getArticles(articleHomeDto, type);
        return ResponseResult.okResult(articles);
    }


    @Override
    public ResponseResult saveArticle(ArticleDto dto){
        //1.检查参数
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        // todo 操作者的身份验证？

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto,apArticle);

        if(dto.getId() == null){
            //新插入文章的情况
            //保存文章
            save(apArticle);
            //保存配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            articleConfigMapper.insert(apArticleConfig);
            //保存 文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            articleContentMapper.insert(apArticleContent);
        }else {
            //修改文章的情况
            updateById(apArticle);
            ApArticleContent apArticleContent = articleContentMapper.selectOne(Wrappers.<ApArticleContent>lambdaQuery().eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            articleContentMapper.updateById(apArticleContent);
        }
        proxy.buildArticleToMinIO(apArticle,dto.getContent());
        return ResponseResult.okResult(apArticle.getId());
    }

    @Async
    void buildArticleToMinIO(ApArticle apArticle, String content) {
        //4.1 获取文章内容
        if(StringUtils.isBlank(content)){
            throw new CustomException(AppHttpCodeEnum.PARAM_INVALID);
        }
        //4.2 文章内容通过freemarker生成html文件
        Template template = null;
        StringWriter out = new StringWriter();
        try {
            template = configuration.getTemplate("article.ftl");
            //数据模型
            Map<String, Object> contentDataModel = new HashMap();
            contentDataModel.put("content", JSONArray.parseArray(content));
            //合成
            template.process(contentDataModel, out);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //4.3 把html文件上传到minio中
        InputStream in = new ByteArrayInputStream(out.toString().getBytes());
        String path = fileStorageService.uploadHtmlFile("", apArticle.getId() + ".html", in);

        //4.4 修改ap_article表，保存static_url字段
        update(Wrappers.<ApArticle>lambdaUpdate().eq(ApArticle::getId, apArticle.getId())
                .set(ApArticle::getStaticUrl, path));
    }
}
