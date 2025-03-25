package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.LogParam;
import com.briup.cms.bean.dto.LogExportParam;
import com.briup.cms.bean.vo.LogVO;

import java.util.List;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/19:07
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
public interface ILogService {
    IPage<LogVO> query(LogParam param);

    List<LogVO> queryForExport(LogExportParam param);

}
