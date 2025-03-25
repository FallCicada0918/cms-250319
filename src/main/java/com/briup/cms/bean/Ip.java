package com.briup.cms.bean;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/18:44
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * ip实体对象
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Accessors(chain = true)
public class Ip {
    /**
     * IP地址
     */
    private String ip;
    /**
     * 省
     */
    private String pro;
    /**
     * 省编码
     */
    private String proCode;
    /**
     * 城市
     */
    private String city;
    /**
     * 城市编码
     */
    private String cityCode;
    /**
     * 区
     */
    private String region;
    /**
     * 区编码
     */
    private String regionCode;
    /**
     * 详细地址 + 运营商
     */
    private String addr;
    /*
     * 主要用于接参，无实际意义
     */
    private String regionNames;
    private String err;
}
