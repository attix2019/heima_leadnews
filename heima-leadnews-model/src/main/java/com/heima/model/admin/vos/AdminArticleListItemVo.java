package com.heima.model.admin.vos;

import com.heima.model.wemedia.pojos.WmNews;
import lombok.Data;

@Data
public class AdminArticleListItemVo extends WmNews {

    private String authorName;
}
