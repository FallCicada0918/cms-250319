package com.briup.cms.web.controller;

import com.briup.cms.config.UploadUtils;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author briup
 * @Description 文件上传接口
 * @date 2023/8/18-16:37
 */
@Api(tags = "文件上传模块")
@Slf4j
@RestController
public class UploadController {
    @Autowired
    private UploadUtils uploadUtils;
    @ApiOperation("文件上传")
    //@PostMapping("/upload")
    @ApiImplicitParam(name = "Authorization", value = "用户令牌", required = true, paramType = "header")
    @PostMapping("/auth/upload")
    @SneakyThrows  //帮助处理 编译时异常
    public Result upload(@RequestPart MultipartFile img){
        return Result.success(uploadUtils.fileToOSS(img));
    }
}