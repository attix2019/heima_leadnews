package com.heima.admin.controller;


import com.heima.admin.service.AdminUserService;
import com.heima.model.admin.dtos.AdminUserLoginDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/login")
public class UserController {

    @Autowired
    AdminUserService adminUserService;

    @PostMapping("/in")
    ResponseResult login(@RequestBody AdminUserLoginDto adminUserLoginDto){
        return adminUserService.login(adminUserLoginDto);
    }
}
