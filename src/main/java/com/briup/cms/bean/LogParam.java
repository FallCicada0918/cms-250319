package com.briup.cms.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/19:03
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
@Data
public class LogParam {
    private Integer pageNum;
    private Integer pageSize;
    //发送请求的用户
    private String username;
    //请求的url
    private String requestUrl;
    //日志时间
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone =
            "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone =
            "GMT+8")
    private LocalDateTime endTime;
}