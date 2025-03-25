package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.LogParam;
import com.briup.cms.bean.dto.LogExportParam;
import com.briup.cms.bean.vo.LogVO;
import com.briup.cms.service.ILogService;
import com.briup.cms.util.Result;
import com.briup.cms.util.execl.ExcelUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    @Autowired
    private ExcelUtils excelUtils;

    @ApiOperation(value = "分页+条件查询日志信息", notes = "用户名、 时间范围可以为空")
    @PostMapping("/query")
    public Result query(@RequestBody LogParam param) {
        IPage<LogVO> page = logService.query(param);
        return Result.success(page);
    }

    //文件以响应 流的方式输出,不需要返回值
    @ApiOperation("导出日志信息")
    @GetMapping(value = "/export", produces = "application/octetstream")
    public void export(HttpServletResponse response, LogExportParam logExportParam) {
        //1.获取数据
        List<LogVO> list =
                logService.queryForExport(logExportParam);
        //2.导出数据
        excelUtils.exportExcel(response, list, LogVO.class, "日志信息表");
    }

}

