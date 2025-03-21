package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
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

    //分页+条件查询(根据parent_id)
    @Override
    public IPage<Category> query(Integer pageNum, Integer pageSize,
                                 Integer parentId) {
        //1.参数判断
        if (pageNum == null || pageNum <= 0 || pageSize == null ||
                pageSize <= 0)
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        //2.分页查询 设置排序次序
        //  select * from cms_category where deleted = 0 andparent_id = ? order by parent_id, order_num;
        IPage<Category> p = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<Category> qw = new LambdaQueryWrapper<>();
        qw.eq(parentId != null, Category::getParentId, parentId);
        qw.orderByAsc(Category::getParentId)
                .orderByAsc(Category::getOrderNum);
        categoryDao.selectPage(p, qw);
        if (p.getTotal() == 0)
            throw new
                    ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        return p;
    }

    //更新栏目
    @Override
    public void update(Category category) {
        //1.id判断：不能为空 必须有效
        Integer id = category.getId();
        Category oldCategory = categoryDao.selectById(id);
        if (id == null || oldCategory == null)
            throw new
                    ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        //2.name判断：如果存在则必须唯一
        LambdaQueryWrapper<Category> qw =
                new LambdaQueryWrapper<>();
        //当待修改的栏目名字与原栏目名不一致时,需要判断待修改的名字在数据库中是否已被使用
        String cname = category.getName();
        if (cname != null &&
                !cname.equals(oldCategory.getName())) {
            qw.eq(Category::getName, cname);
            if (categoryDao.selectOne(qw) != null)
                throw new
                        ServiceException(ResultCode.CATEGORYNAME_HAS_EXISTED);
        }
        Integer parentId = category.getParentId();
        //3.如果当前栏目为1级，则不能更改为2级
        if (oldCategory.getParentId() == null &&
                parentId != null) {
            throw new
                    ServiceException(ResultCode.CATEGORY_LEVEL_SETTING_ERROR);
        }
        //4.如果需要修改的栏目为2级，且要修改其父栏目
        if (oldCategory.getParentId() != null &&
                parentId != null) {
            Category pCategory =
                    categoryDao.selectById(parentId);
            // 需要更新的父栏目不存在，或 需要更新的父栏目为2级栏目，则失败
            if (pCategory == null ||
                    pCategory.getParentId() != null)
                throw new
                        ServiceException(ResultCode.PCATEGORY_IS_INVALID);
        }
        //5.执行更新操作
        categoryDao.updateById(category);
    }
}
