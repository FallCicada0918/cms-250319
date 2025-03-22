package com.briup.cms.bean;

import com.alibaba.excel.annotation.ExcelIgnore;
import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ColumnWidth;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
//针对栏目名称重新Equals和HashCode方法
@EqualsAndHashCode(callSuper = false, of = "name")
@ColumnWidth(12)
@TableName("cms_category")
public class Category implements Serializable {
    private static final long serialVersionUID = 1L;

    /**
     * 栏目编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    @ExcelIgnore
    private Integer id;

    /**
     * 栏目名称
     */
    @ExcelProperty("栏目名称")
    private String name;

    /**
     * 栏目描述
     */
    @ExcelProperty("栏目描述")
    private String description;

    /**
     * 栏目序号
     */
    @ExcelProperty(value = "栏目序号")
    //@TableField(value = "order_num")
    private Integer orderNum;

    /**
     * 栏目删除状态
     */
    @TableLogic
    @ExcelProperty(value = "栏目删除状态",
            converter = com.briup.cms.util.execl.DeletedConverter.class)
    @ColumnWidth(20)
    private Integer deleted;

    /**
     * 父栏目id
     */
    @ExcelProperty(value = "父栏目",
            converter = com.briup.cms.util.execl.CategoryParentIdConverter.class)
    private Integer parentId;
}
