package com.briup.cms.bean.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/20/14:36
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginParam {
    private String username;
    private String password;
}