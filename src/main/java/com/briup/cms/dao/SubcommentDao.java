package com.briup.cms.dao;

import com.briup.cms.bean.Subcomment;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.briup.cms.bean.extend.SubCommentExtend;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
public interface SubcommentDao extends BaseMapper<Subcomment> {
    //查询指定1级评论下的所有2级评论(含作者)
    List<SubCommentExtend> queryByParentId(@Param("parentId") Long id);


}
