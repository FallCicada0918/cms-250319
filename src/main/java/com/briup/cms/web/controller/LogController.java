package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.LogParam;
import com.briup.cms.bean.vo.LogVO;
import com.briup.cms.service.ILogService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Api(tags = "日志模块")
@RestController
@RequestMapping("/log")
public class LogController {
    @Autowired
    private ILogService logService;

    @ApiOperation(value = "分页+条件查询日志信息", notes = "用户名、 时间范围可以为空")
    @PostMapping("/query")
    public Result query(@RequestBody LogParam param) {
        IPage<LogVO> page = logService.query(param);
        return Result.success(page);
    }
}

