package com.heima.admin.service;

import com.heima.model.admin.dtos.AdminUserLoginDto;
import com.heima.model.common.dtos.ResponseResult;

public interface AdminUserService {

    ResponseResult login(AdminUserLoginDto adminUserLoginDto);
}
