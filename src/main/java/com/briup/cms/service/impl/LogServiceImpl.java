package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Log;
import com.briup.cms.bean.LogParam;
import com.briup.cms.bean.vo.LogVO;
import com.briup.cms.dao.LogDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.ILogService;
import com.briup.cms.util.BeanCopyUtils;
import com.briup.cms.util.Result;
import com.briup.cms.util.ResultCode;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/19:05
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
@Service
public class LogServiceImpl implements ILogService {
    @Autowired
    private LogDao logDao;
    @Autowired
    private Gson gson;
    @Override
    public IPage<LogVO> query(LogParam param) {
        // 1.参数判断
        if (param == null || param.getPageNum() == null ||
                param.getPageSize() == null)
            throw new
                    ServiceException(ResultCode.PARAM_IS_BLANK);
        // 2.查询条件准备，按时间倒序
        IPage<Log> page = new Page<>(param.getPageNum(),
                param.getPageSize());
        //获取查询条件
        LambdaQueryWrapper<Log> wrapper =
                getQueryWrapper(param.getUsername(), param.getRequestUrl(),param.getStartTime(), param.getEndTime());
        // 3.执行分页查询
        logDao.selectPage(page, wrapper);
        // 前端展示时所需的参数有限,所以在返回前将 将Log分页对象 转换为LogVO分页对象
        IPage<LogVO> logVOPage = BeanCopyUtils.copyPage(page,
                LogVO.class);
        //前端展示无需转换ResultJson中的data数据,所以resultJson 拆分成 code 及 msg
        parseResultJson(logVOPage.getRecords());
        return logVOPage;
    }
    private void parseResultJson(List<LogVO> list){
        list.forEach(logVO -> {
            Result result = gson.fromJson(logVO.getResultJson(),
                    Result.class);
            logVO.setCode(result.getCode());
            logVO.setMsg(result.getMsg());
            logVO.setResultJson(null);
        });
    }
    private LambdaQueryWrapper<Log> getQueryWrapper(String
                                                            username, String url, LocalDateTime startTime, LocalDateTime
                                                            endTime) {
        LambdaQueryWrapper<Log> wrapper = new
                LambdaQueryWrapper<>();
        wrapper.eq(StringUtils.hasText(username),
                        Log::getUsername, username)
                .eq(StringUtils.hasText(url), Log::getRequestUrl,
                        url)
                .le(endTime != null, Log::getCreateTime, endTime)
                .ge(startTime != null, Log::getCreateTime, startTime)
                .orderByDesc(Log::getCreateTime);
        return wrapper;
    }
}