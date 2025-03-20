package com.briup.cms.web.controller;


import com.briup.cms.bean.User;
import com.briup.cms.bean.vo.LoginParam;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.MD5Utils;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import com.briup.cms.service.IUserService;
/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Slf4j
@Api(tags = "登陆模块")
@RestController
@RequestMapping("/log")
public class LogController {

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
        log.info("用户信息：{}",map);
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
    @PostMapping(value = "/login_old", consumes = "application/xwww-form-urlencoded")
    public Result login(String username,
                        @RequestParam("password") String passwd) {
        User user = userService.login(username, passwd);
        return Result.success(user);
    }
}

