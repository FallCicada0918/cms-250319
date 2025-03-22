package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Category;
import com.briup.cms.bean.extend.CategoryExtend;
import com.briup.cms.service.ICategoryService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

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

    //工具类对象
    @Autowired
    private com.briup.cms.util.execl.ExcelUtils excelUtils;//工具类对象

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

    @ApiOperation(value = "分页查询所有栏目")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "pageNum", value = "当前页",
                    dataType = "int", required = true, defaultValue = "1", paramType
                    = "query"),
            @ApiImplicitParam(name = "pageSize", value = "每页数量",
                    dataType = "int", required = true, defaultValue = "4", paramType
                    = "query"),
            @ApiImplicitParam(name = "parentId", value = "父栏目id",
                    dataType = "int", paramType = "query")
    })
    @GetMapping("/query")
    public Result query(Integer pageNum, Integer pageSize, Integer
            parentId) {
        IPage<Category> p = categoryService.query(pageNum, pageSize,
                parentId);
        return Result.success(p);
    }

    @ApiOperation(value = "更新栏目", notes = "栏目名必须唯一，栏目级别不能改动")
    @PutMapping("/update")
    public Result update(@RequestBody Category category) {
        categoryService.update(category);
        return Result.success("修改成功");
    }

    @ApiOperation(value = "根据id删除栏目", notes = "id必须存在且有效")
    @DeleteMapping("/deleteById/{id}")
    public Result deleteById(@PathVariable Integer id) {
        categoryService.deleteById(id);

        return Result.success("删除成功");

    }

    @ApiOperation(value = "批量删除栏目", notes = "需要提供一个或多个id值")
    @DeleteMapping("/deleteByBatch/{ids}")
    public Result deleteCategoryInBatch(@PathVariable("ids")
                                        List<Integer> ids) {
        categoryService.deleteInBatch(ids);
        return Result.success("删除成功");
    }

    @ApiOperation("导出栏目数据")
    //注意：excel导出，必须指定请求头"content-type"为"application/octet-stream"
    @GetMapping(value = "/export", produces = "application/octet-stream")
    public void exports(HttpServletResponse response) {
        //1.获取数据库中所有1级栏目，赋值给转换器
        com.briup.cms.util.execl.CategoryParentIdConverter.pCateList = categoryService.queryAllOneLevel();
        //2.获取栏目数据
        List<Category> list = categoryService.queryAll();
        //3.导出数据
        excelUtils.exportExcel(response, list, Category.class, "栏目信息表");
    }

    @ApiOperation("获取所有父栏目")
    @GetMapping("/queryAllOneLevel")
    public Result queryAllParentWithoutTwo() {
        List<Category> list = categoryService.queryAllOneLevel();
        return Result.success(list);
    }

    @ApiOperation(value = "查询所有1级栏目(含2级)", notes = "不需要分页")
    @GetMapping("/queryAllParent")
    public Result queryAllParent() {
        List<CategoryExtend> list = categoryService.queryAllParent();
        return Result.success(list);
    }

}


