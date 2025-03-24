package com.briup.cms.bean.vo;

import lombok.Data;

@Data
public class CommentDeleteParam {
    private Long id;        //待删除评论id
    private Integer type;   //待删除评论类型：1|2级评论
}
