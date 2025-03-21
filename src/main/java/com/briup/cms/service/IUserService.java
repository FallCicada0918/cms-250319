package com.briup.cms.service;

import com.briup.cms.bean.User;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
public interface IUserService {
    User login(String username, String password);

    User queryById(Long id);
}
