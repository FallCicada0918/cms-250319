package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Category;
import com.briup.cms.bean.extend.CategoryExtend;

import java.util.List;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/21/11:21
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
public interface ICategoryService {
    void insert(Category category);

    Category getCategoryById(Integer id);

    IPage<Category> query(Integer pageNum, Integer pageSize,
                          Integer parentId);

    void update(Category category);

    void deleteById(Integer id);

    void deleteInBatch(List<Integer> ids);

    List<Category> queryAllOneLevel();

    List<Category> queryAll();

    List<CategoryExtend> queryAllParent();
}
