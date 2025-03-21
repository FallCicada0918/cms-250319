package com.briup.cms.dao;

import com.briup.cms.bean.Category;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
public interface CategoryDao extends BaseMapper<Category> {
    //查询栏目表中最大的order_num值
    Integer getMaxOrderNum();


}
