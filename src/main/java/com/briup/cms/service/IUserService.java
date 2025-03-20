package com.briup.cms.service;

import com.briup.cms.bean.User;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/20/14:47
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
public interface IUserService {
    User login(String username, String password);

    User queryById(Long id);


}
