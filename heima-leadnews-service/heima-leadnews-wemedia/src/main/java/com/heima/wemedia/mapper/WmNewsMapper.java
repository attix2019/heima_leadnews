package com.heima.wemedia.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.heima.model.admin.dtos.AdminArticlePageDto;
import com.heima.model.admin.vos.AdminArticleListItemVo;
import com.heima.model.wemedia.pojos.WmNews;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface WmNewsMapper extends BaseMapper<WmNews> {

    Page<AdminArticleListItemVo> listArticlesForAdmin(Page<AdminArticleListItemVo> page, AdminArticlePageDto dto);
}
