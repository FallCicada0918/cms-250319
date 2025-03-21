package com.briup.cms.web.controller;


import com.briup.cms.service.IUserService;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Api(tags = "用户模块")
@RestController
@Slf4j
@RequestMapping("/auth/user")
public class UserController {
    @Autowired
    private IUserService userService;

/*
    //admin使用：用户登录后首页展示个人信息需要使用
    @ApiOperation("获取用户个人信息")
    @GetMapping("/info")  //@RequestAttribute 请参考JwtInterceptor中 38行代码
    public Result getInfo(@RequestAttribute("userId") Long id){
        log.info("id:{}",id);
        return Result.success(userService.queryById(id));
    }
*/

    @ApiOperation("获取用户个人信息")
    @GetMapping("/info")
    public Result getInfo(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization");
        log.info("jwt: {}", jwt);
        long id = JwtUtil.getUserId(jwt);
        log.info("id: {}", id);
        return Result.success(userService.queryById(id));
    }
}

