package com.briup.cms.web.controller;


import com.briup.cms.bean.Category;
import com.briup.cms.service.ICategoryService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Api(tags = "栏目模块")
@RestController
@RequestMapping("/auth/category")
public class CategoryController {
    @Autowired
    private ICategoryService categoryService;

    @ApiOperation(value = "新增栏目", notes = "栏目名必须唯一，如果为二级栏目则其父栏目id必须有效")
    @PostMapping("/save")
    public Result save(@RequestBody Category category) {
        categoryService.insert(category);
        return Result.success("新增成功");
    }

    @ApiOperation("根据id查询栏目信息")
    @GetMapping("/queryById/{id}")
    public Result getCategoryById(@PathVariable("id") Integer id) {
        Category category = categoryService.getCategoryById(id);
        return Result.success(category);
    }

}


