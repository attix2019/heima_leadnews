package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
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
    public ResponseResult deleteMaterial(@PathVariable Integer id){
        return wmMaterialService.deleteMaterial(id);
    }

}
