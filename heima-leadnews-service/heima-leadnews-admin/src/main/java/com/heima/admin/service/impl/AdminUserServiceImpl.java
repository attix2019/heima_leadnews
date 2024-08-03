package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdminUserMapper;
import com.heima.admin.service.AdminUserService;
import com.heima.model.admin.dtos.AdminUserLoginDto;
import com.heima.model.admin.pojos.AdminUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class AdminUserServiceImpl extends ServiceImpl<AdminUserMapper, AdminUser> implements AdminUserService {
    @Override
    public ResponseResult login(AdminUserLoginDto dto) {
        //1.正常登录（手机号+密码登录）
        if (dto == null || StringUtils.isBlank(dto.getName()) || StringUtils.isBlank(dto.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        // 查询用户
        AdminUser adminUser = getOne(Wrappers.<AdminUser>lambdaQuery().eq(AdminUser::getName, dto.getName()));
        if (adminUser == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST, "用户不存在");
        }

        // 比对密码
        String salt = adminUser.getSalt();
        String pswd = dto.getPassword();
        pswd = DigestUtils.md5DigestAsHex((pswd + salt).getBytes());
        if (!pswd.equals(adminUser.getPassword())) {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
        //1.3 返回数据  jwt
        Map<String, Object> map = new HashMap<>();
        map.put("token", AppJwtUtil.getToken(adminUser.getId().longValue()));
        adminUser.setSalt("");
        adminUser.setPassword("");
        map.put("user", adminUser);
        return ResponseResult.okResult(map);
    }
}
