package com.briup.cms.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.extend.CommentExtend;
import com.briup.cms.bean.extend.SubCommentExtend;
import com.briup.cms.bean.vo.CommentDeleteParam;
import com.briup.cms.bean.vo.CommentQueryParam;

import java.util.List;

public interface ICommentService {
    void saveComment(Comment comment);

    void saveSubComment(Subcomment comment);

    void deleteById(CommentDeleteParam param);

    void deleteInBatch(List<CommentDeleteParam> list);

    List<SubCommentExtend> queryByCommentId(Long id);

    IPage<CommentExtend> queryByArticleId(Integer pageNum, Integer pageSize, Long aId);

    IPage<CommentExtend> query(CommentQueryParam param);
}
