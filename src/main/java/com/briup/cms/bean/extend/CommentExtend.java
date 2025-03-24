package com.briup.cms.bean.extend;

import com.briup.cms.bean.Comment;
import com.briup.cms.bean.User;
import lombok.Data;

import java.util.List;

@Data
public class CommentExtend extends Comment {
    //评论发表人
    private User author;
    //其下所有2级评论
    private List<SubCommentExtend> childComments;
}
