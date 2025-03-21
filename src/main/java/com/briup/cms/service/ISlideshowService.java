package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Slideshow;

import java.util.List;

public interface ISlideshowService {
    List<Slideshow> queryAllEnable();

    IPage<Slideshow> query(Integer pageNum, Integer pageSize, String status, String desc);

    Object queryById(Integer id);

    void saveOrUpdate(Slideshow slideshow);

    void deleteInBatch(List<Integer> ids);
}