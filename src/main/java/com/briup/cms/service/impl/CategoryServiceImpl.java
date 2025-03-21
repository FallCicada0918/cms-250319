package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.briup.cms.bean.Category;
import com.briup.cms.dao.CategoryDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.ICategoryService;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/21/11:19
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
@Slf4j
@Service
public class CategoryServiceImpl implements ICategoryService {
    @Autowired
    private CategoryDao categoryDao;

    @Override
    public void insert(Category category) {
        //判断 category==null【可不写，前端会处理】
        if (category == null)
            throw new
                    ServiceException(ResultCode.PARAM_IS_BLANK);
        //1.判断 栏目名是否唯一
        LambdaQueryWrapper<Category> qw = new
                LambdaQueryWrapper<>();
        qw.eq(Category::getName, category.getName());
        Category c = categoryDao.selectOne(qw);
        if (c != null)
            throw new
                    ServiceException(ResultCode.CATEGORYNAME_HAS_EXISTED);
        //2.如果包含父栏目，则判断parentId是否有效(存在且为1级栏目)
        Integer parentId = category.getParentId();
        if (parentId != null) {
            Category pCate = categoryDao.selectById(parentId);
            if(pCate == null || pCate.getParentId() != null)
                throw new
                        ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        //3.获取max(order_num)
        // 栏目表中无数据，新插入栏目序号为1
        int order_num = 1;
        // 栏目表中有数据，新插入栏目序号 = max(order_num) + 1;
        if (categoryDao.selectCount(null) != 0) {
            //借助自定义sql语句实现，【简单，推荐】
            Integer mo = categoryDao.getMaxOrderNum();
            log.info("max_order_num: ", mo);
            order_num = mo + 1;
        }
        //4.设置order_num值
        category.setOrderNum(order_num);
        //5.插入
        categoryDao.insert(category);
    }

    @Override
    public Category getCategoryById(Integer id) {
        Category category = categoryDao.selectById(id);
        if(category == null)
            throw new
                    ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        return category;
    }
}
