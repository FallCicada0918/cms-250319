package com.briup.cms.bean.vo;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/19:04
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * @Description 日志展示实体
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LogVO {
    /**
     * 操作用户
     */
    @ExcelProperty("操作用户")
    private String username;
    /**
     * 接口描述信息
     */
    @ExcelProperty("接口描述信息")
    private String businessName;
    /**
     * 请求接口
     */
    @ExcelProperty("请求接口")
    private String requestUrl;
    /**
     * 请求方式
     昆山杰普软件科技有限公司 No. 19/33
     */
    @ExcelProperty("请求方式")
    private String requestMethod;
    /**
     * ip
     */
    @ExcelProperty("ip")
    private String ip;
    /**
     * 请求接口耗时
     */
    @ExcelProperty("请求接口耗时")
    private Long spendTime;
    /**
     * 创建时间
     */
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone =
            "GMT+8")
    private LocalDateTime createTime;
    /**
     * 响应参数
     */
    @ExcelIgnore
    private String resultJson;
    /**
     * 响应状态码
     */
    @ExcelProperty("响应状态码")
    private Integer code;
    /**
     * 响应消息
     */
    @ExcelProperty("响应消息")
    private String msg;
}