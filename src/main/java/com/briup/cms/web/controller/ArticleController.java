package com.briup.cms.web.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.extend.ArticleExtend;
import com.briup.cms.bean.vo.ArticleParam;
import com.briup.cms.service.IArticleService;
import com.briup.cms.util.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Slf4j
@Api(tags = "资讯模块")
@RestController
@RequestMapping("/auth/article")
public class ArticleController {
    @Autowired
    private IArticleService articleService;

    @ApiOperation(value = "新增或修改文章",
            notes = "文章id存在为修改，不存在为新增")
    @PostMapping("/saveOrUpdate")
    public Result saveOrUpdate(@RequestBody Article article) {
        articleService.saveOrUpdate(article);

        return Result.success("新增或修改成功");
    }

    @ApiOperation(value = "审核文章",
            notes = "文章id必须有效，status: 审核通过、审核不通过")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "文章id",
                    required = true, dataType = "Long"),
            @ApiImplicitParam(name = "status", value = "审核状态",
                    required = true, dataType = "String")
    })
    @PutMapping("/review")
    public Result reviewArticle(Long id, String status) {
        articleService.reviewArticle(id, status);

        return Result.success("审核完成");
    }

    @ApiOperation(value = "批量删除文章",notes = "需要提供多个id值")
    @DeleteMapping("/deleteByBatch/{ids}")
    public Result deleteInBatch(@PathVariable List<Long> ids) {
        log.info("deleteByBatch ids: " + ids);
        articleService.deleteInBatch(ids);

        return Result.success("删除成功");
    }

    @ApiOperation(value = "查询指定文章",
            notes = "文章要包含3条一级评论及作者")
    @GetMapping("/queryById/{id}")
    public Result queryById(@PathVariable Long id) {
        ArticleExtend articleExtend =
                articleService.queryByIdWithCommentsAndUser(id);

        return Result.success(articleExtend);
    }

    @ApiOperation(value = "分页+条件查询文章(含作者)",
            notes = "该接口可同时提供给用户端管理员使用")
    @PostMapping("/query")
    public Result queryById(@RequestBody ArticleParam articleParam) {
        IPage<ArticleExtend> page = articleService.query(articleParam);
        return Result.success(page);
    }
}

