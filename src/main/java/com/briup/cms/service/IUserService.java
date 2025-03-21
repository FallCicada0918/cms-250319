package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.UserExtend;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

public interface IUserService {
    User login(String username, String passwd);

    User queryById(Long id);

    void save(User user);

    void setVip(Long id);

    void update(User user);

    void deleteByBatch(List<Long> ids);

    IPage<UserExtend> query(Integer pageNum, Integer pageSize, String username, String status, Integer roleId, Integer isVip);
}
