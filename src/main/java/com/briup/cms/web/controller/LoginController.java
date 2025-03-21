package com.briup.cms.web.controller;

import com.briup.cms.bean.LoginParam;
import com.briup.cms.bean.User;
import com.briup.cms.service.IUserService;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.MD5Utils;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@Api(tags = "登录模块")
@RestController
public class LoginController {
    @Autowired
    private IUserService userService;

    @ApiOperation(value = "登录", notes = "需要提供用户名和密码")
    @PostMapping(value = "/login")
    public Result login(@RequestBody LoginParam param) {
        //对密码加密与数据库进行比对
        User user = userService.login(param.getUsername(),
                MD5Utils.MD5(param.getPassword()));
        //新增token 相关代码
        Map<String,Object> map = new HashMap<>();
        map.put("userId",user.getId()+"");
        map.put("username", user.getUsername());
        // 放入isVip不合适，后期充值成功后，用户不会重新登录，token值不变，导致isVip值是错误的
        map.put("isVip", user.getIsVip());
        map.put("roleId", user.getRoleId());
        String token = JwtUtil.generateJwt(map);
        return Result.success(token);
    }

    @ApiOperation(value = "登录", notes = "需要提供用户名和密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "username", value = "用户名",
                    paramType = "form", dataType = "String", required = true,
                    defaultValue = "tom"),
            @ApiImplicitParam(name = "password", value = "密码",
                    paramType = "form", dataType = "String", required = true,
                    defaultValue = "123456")
    }) //Post请求 + form提交 + consumes设置表单字符串
    @PostMapping(value = "/login_old", consumes = "application/x-www-form-urlencoded")
    public Result login(String username,
            @RequestParam("password") String passwd) {
            User user = userService.login(username, passwd);
            return Result.success(user);
    }

    @ApiOperation(value = "退出登录")
    @PostMapping(value = "/logout")
    public Result logout() {
        return Result.success();
    }
}