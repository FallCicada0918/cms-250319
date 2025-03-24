package com.briup.cms.bean.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.time.LocalDateTime;

//文章分页+条件查询参数类
@Data
public class ArticleParam {
    private Integer pageNum;
    private Integer pageSize;

    private Integer categoryId;
    private String title;
    private String status;
    private Long userId;
    private Integer charged;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime startTime;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime endTime;
}