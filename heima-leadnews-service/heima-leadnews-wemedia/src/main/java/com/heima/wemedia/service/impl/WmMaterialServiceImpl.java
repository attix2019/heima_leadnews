package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.file.service.FileStorageService;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.model.wemedia.pojos.WmMaterial;
import com.heima.model.wemedia.pojos.WmNewsMaterial;
import com.heima.wemedia.mapper.WmMaterialMapper;
import com.heima.wemedia.mapper.WmNewsMaterialMapper;
import com.heima.wemedia.service.WmMaterialService;
import com.heima.wemedia.util.WmThreadLocalUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.UUID;

@Service
@Slf4j
public class WmMaterialServiceImpl extends ServiceImpl<WmMaterialMapper, WmMaterial> implements WmMaterialService {

    @Autowired
    FileStorageService fileStorageService;

    @Autowired
    WmMaterialMapper wmMaterialMapper;

    @Autowired
    WmNewsMaterialMapper wmNewsMaterialMapper;

    @Override
    public ResponseResult uploadPicture(MultipartFile multipartFile) {
        //1.检查参数
        if(multipartFile == null || multipartFile.getSize() == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.上传图片到minIO中
        String fileName = UUID.randomUUID().toString().replace("-", "");
        //aa.jpg
        String originalFilename = multipartFile.getOriginalFilename();
        String postfix = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileId = null;
        try {
            fileId = fileStorageService.uploadImgFile("", fileName + postfix, multipartFile.getInputStream());
            log.info("上传图片到MinIO中，fileId:{}",fileId);
        } catch (IOException e) {
            e.printStackTrace();
            log.error("WmMaterialServiceImpl-上传文件失败");
        }

        //3.保存到数据库中
        WmMaterial wmMaterial = new WmMaterial();
        wmMaterial.setUserId(WmThreadLocalUtils.getUser().getId());
        wmMaterial.setUrl(fileId);
        wmMaterial.setIsCollection((short)0);
        wmMaterial.setType((short)0);
        wmMaterial.setCreatedTime(new Date());
        save(wmMaterial);

        //4.返回结果

        return ResponseResult.okResult(wmMaterial);
    }

    @Override
    public ResponseResult getMaterialList(WmMaterialDto dto) {
        //1.检查参数
        dto.checkParam();
        //2.分页查询
        IPage page = new Page(dto.getPage(),dto.getSize());
        LambdaQueryWrapper<WmMaterial> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //是否收藏
        if(dto.getIsCollection() != null && dto.getIsCollection() == 1){
            lambdaQueryWrapper.eq(WmMaterial::getIsCollection,dto.getIsCollection());
        }

        //按照用户查询
        lambdaQueryWrapper.eq(WmMaterial::getUserId,WmThreadLocalUtils.getUser().getId());
        //按照时间倒序
        lambdaQueryWrapper.orderByDesc(WmMaterial::getCreatedTime);

        page = page(page,lambdaQueryWrapper);

        //3.结果返回
        ResponseResult responseResult = new PageResponseResult(dto.getPage(),dto.getSize(),(int)page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    @Override
    public ResponseResult deleteMaterial(Integer id) {
        WmMaterial wmMaterial = getOne(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if(wmMaterial == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.MATERIAL_STILL_REFERENCED);
        }
        String path = wmMaterial.getUrl();

        // 查询素材是否正在被引用
        int count = wmNewsMaterialMapper.selectCount(Wrappers.<WmNewsMaterial>lambdaQuery().eq(WmNewsMaterial::getMaterialId,
                id));
        if(count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.MATERIAL_STILL_REFERENCED);
        }

        int affected = wmMaterialMapper.delete(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if(affected == 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        fileStorageService.delete(path);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult bookmarkMaterial(Integer id) {
        WmMaterial wmMaterial = getOne(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if(wmMaterial == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        wmMaterial.setIsCollection( (short)1);
        updateById(wmMaterial);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    @Override
    public ResponseResult unBookmarkMaterial(Integer id) {
        WmMaterial wmMaterial = getOne(Wrappers.<WmMaterial>lambdaQuery().eq(WmMaterial::getId, id));
        if(wmMaterial == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        wmMaterial.setIsCollection( (short)0);
        updateById(wmMaterial);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }
}
