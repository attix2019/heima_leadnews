package com.heima.search.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.search.dtos.ArticleSearchDto;
import com.heima.search.service.AssociateWordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/associate")
public class AssociateWordsController {

    @Autowired
    AssociateWordService associateWordServices;

    @PostMapping("/search")
    public ResponseResult search(@RequestBody  ArticleSearchDto articleSearchDto) {
        return associateWordServices.findAssociateWords(articleSearchDto);
    }
}