package com.briup.cms.bean.extend;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/21/16:48
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import com.briup.cms.bean.Category;
import lombok.Data;

import java.util.List;
// 一级栏目扩展类(包含其下所有二级栏目)
@Data
public class CategoryExtend extends Category {
    private List<Category> cates;
}