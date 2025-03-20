package com.briup.cms.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_category")
public class Category implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 栏目编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    /**
     * 栏目名称
     */
    private String name;

    /**
     * 栏目描述
     */
    private String description;

    /**
     * 栏目序号
     */
    private Integer orderNum;

    /**
     * 栏目删除状态
     */
    @TableLogic
    private Integer deleted;

    /**
     * 父栏目id
     */
    private Integer parentId;


}
