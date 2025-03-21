package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.briup.cms.bean.User;
import com.briup.cms.dao.UserDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.IUserService;
import com.briup.cms.util.ResultCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements IUserService {
    @Autowired
    private UserDao userDao;
    @Override
    public User login(String username, String password) {
        //1.条件准备
        LambdaQueryWrapper<User> lqw = new LambdaQueryWrapper<User>();
        lqw.eq(User::getUsername, username).eq(User::getPassword, password);

        //2.查询校验
        User user = userDao.selectOne(lqw);
        if (user == null)
            throw new ServiceException(ResultCode.USER_LOGIN_ERROR);
        return user;
    }

    @Override
    public User queryById(Long id) {
        //1.有效参数判断
        if (id == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

    //2.查找用户
    User user = userDao.selectById(id);
     if (user == null)
                throw new ServiceException(ResultCode.DATA_NONE);
     return user;
}
}
