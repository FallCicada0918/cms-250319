package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.UserExtend;
import com.briup.cms.service.IUserService;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
//import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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

    @ApiOperation(value = "新增用户",
            notes = "username、password必须存在不为空，且username唯一")
    @PostMapping("/save")
    public Result save(@RequestBody User user) {
        userService.save(user);
        return Result.success("新增成功");
    }

    @ApiOperation(value = "根据id查找用户", notes = "id必须存在且有效")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "用户id",
                    required = true, paramType = "path",
                    dataType = "String")
    })
    @GetMapping("/queryById/{id}")
    public Result queryById(@PathVariable Long id) {
        User user = userService.queryById(id);

        return Result.success(user);
    }

    @ApiOperation(value = "设置用户为Vip", notes = "id存在且有效")
    @PutMapping("/setVip/{id}")
    public Result setVip(@PathVariable Long id) {
        userService.setVip(id);

        return Result.success("更新成功");
    }

    @ApiOperation(value = "更新用户信息",
            notes = "id必须存在且有效，如果username存在则必须唯一")
    @PutMapping("/update")
    public Result update(@RequestBody User user) {
        userService.update(user);

        return Result.success("更新成功");
    }

    @ApiOperation(value = "根据id删除用户", notes = "id必须存在且有效")
    @DeleteMapping("/deleteByBatch/{ids}")
    public Result deleteByBatch(@PathVariable("ids") List<Long> ids) {
        userService.deleteByBatch(ids);

        return Result.success("删除成功");
    }

    @ApiOperation(value = "分页+条件查询用户", notes = "用户中要含角色信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "页码", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页条数", required = true, dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态：启用|禁用", dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "roleId", value = "角色id", dataType = "int", paramType = "query"),
            @ApiImplicitParam(name = "isVip", value = "是否为会员: 0|1", dataType = "int", paramType = "query")
    })
    @GetMapping("/query")
    public Result query(Integer pageNum, Integer pageSize,
                        String username, String status,
                        Integer roleId, Integer isVip) {
        IPage<UserExtend> p =
                userService.query(pageNum, pageSize, username, status, roleId, isVip);

        return Result.success(p);
    }
}


