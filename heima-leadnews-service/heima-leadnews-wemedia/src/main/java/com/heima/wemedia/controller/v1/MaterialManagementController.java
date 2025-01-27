package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
@Api(tags = "自媒体作者端素材管理相关接口")
public class MaterialManagementController {

    @Autowired
    WmMaterialService wmMaterialService;

    @PostMapping("/upload_picture")
    public ResponseResult uploadPicture(MultipartFile multipartFile){
        return wmMaterialService.uploadPicture(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult getMaterialList(@RequestBody  WmMaterialDto wmMaterialDto){
        return wmMaterialService.getMaterialList(wmMaterialDto);
    }

    @GetMapping("/del_picture/{id}")
    @ApiOperation("删除素材")
    public ResponseResult deleteMaterial(@PathVariable Integer id){
        return wmMaterialService.deleteMaterial(id);
    }

    @GetMapping("/collect/{id}")
    @ApiOperation("取消收藏")
    public ResponseResult bookmarkMaterial(@PathVariable Integer id){
        return wmMaterialService.bookmarkMaterial(id);
    }

    @GetMapping("/cancel_collect/{id}")
    @ApiOperation("收藏")
    public ResponseResult unBookmarkMaterial(@PathVariable Integer id){
        return wmMaterialService.unBookmarkMaterial(id);
    }

}
