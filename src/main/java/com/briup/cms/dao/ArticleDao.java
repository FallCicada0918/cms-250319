package com.briup.cms.dao;

import com.briup.cms.bean.Article;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
public interface ArticleDao extends BaseMapper<Article> {

    //根据2级栏目id查询有效的资讯数量(发布的用户存在)
    Integer getArticleNumByCategoryId(Integer categoryId);

}
