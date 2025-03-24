package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.Subcomment;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.CommentExtend;
import com.briup.cms.bean.extend.SubCommentExtend;
import com.briup.cms.bean.vo.CommentDeleteParam;
import com.briup.cms.bean.vo.CommentQueryParam;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.dao.CommentDao;
import com.briup.cms.dao.SubcommentDao;
import com.briup.cms.dao.UserDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.ICommentService;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
public class CommentServiceImpl implements ICommentService {

    @Autowired
    private CommentDao commentDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private SubcommentDao subcommentDao;

    @Override
    public void saveComment(Comment comment) {
        // 1.参数判断
        if(comment == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.用户存在及禁用状态判断
        User user = userDao.selectById(comment.getUserId());
        if(user == null)
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        if("禁用".equals(user.getStatus()))
            throw new ServiceException(ResultCode.USER_ACCOUNT_FORBIDDEN);

        // 3.文章存在判断
        if(articleDao.selectById(comment.getArticleId()) == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);

        // 4.发表时间准备
        comment.setPublishTime(LocalDateTime.now());

        // 5.发表评论
        commentDao.insert(comment);
    }

    @Override
    public void saveSubComment(Subcomment comment) {
        // 1.参数判断
        if(comment == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.用户存在及禁用状态判断
        User user = userDao.selectById(comment.getUserId());
        if(user == null)
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        if("禁用".equals(user.getStatus()))
            throw new ServiceException(ResultCode.USER_ACCOUNT_FORBIDDEN);

        // 3.一级评论存在判断
        if(commentDao.selectById(comment.getParentId()) == null)
            throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);

        // 4.回复评论id存在判断【replyId表示当前评论要回复的2级评论id】
        Long replyId = comment.getReplyId();
        if(replyId != null &&
                subcommentDao.selectById(replyId) == null)
            throw new ServiceException(ResultCode.REPLYCOMMENT_NOT_EXIST);

        // 5.发表时间准备
        comment.setPublishTime(LocalDateTime.now());

        // 6.发表二级评论
        subcommentDao.insert(comment);
    }

    @Override
    public void deleteById(CommentDeleteParam param) {
        // 1.参数判断
        Long id = param.getId();
        Integer type = param.getType();
        if(param == null || type == null || id == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.类型判断
        if(type == 1) {
            if(commentDao.selectById(id) == null)
                throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);

            // 删除一级评论
            commentDao.deleteById(id);
        }else if(type == 2) {
            if(subcommentDao.selectById(id) == null)
                throw new ServiceException(ResultCode.SUBCOMMENT_NOT_EXIST);

            // 删除二级评论
            subcommentDao.deleteById(id);
        }else {
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
    }

    @Override
    public void deleteInBatch(List<CommentDeleteParam> list) {
        //1.参数判断
        if(list == null || list.size() == 0)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        //2.逐条评论删除，主要只要成功删除1条评论，该方法就算执行成功
        // 评论删除标记
        boolean flag = false;
        for(CommentDeleteParam comment : list) {
            try {
                deleteById(comment);
                flag = true;
            }catch (Exception e) {
                e.printStackTrace();
            }
        }

        //3.1条都不成功，则抛出异常
        if(!flag)
            throw new ServiceException(ResultCode.COMMENT_DELETE_BATCH_FAILURE);
    }

    @Override
    public List<SubCommentExtend> queryByCommentId(Long id) {
        // 1.一级评论id必须存在
        Comment comment = commentDao.selectById(id);
        if(comment == null)
            throw new ServiceException(ResultCode.COMMENT_NOT_EXIST);

        // 2.一级评论所属的文章必须存在
        Long articleId = comment.getArticleId();
        if(articleDao.selectById(articleId) == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);

        // 3.自定义sql语句实现查询所有2级评论(含作者)
        List<SubCommentExtend> list = subcommentDao.queryByParentId(id);

        if(list == null || list.size() == 0)
            throw new ServiceException(ResultCode.SUBCOMMENT_NOT_EXIST);

        return list;
    }

    @Override
    public IPage<CommentExtend> queryByArticleId(Integer pageNum,
                                                 Integer pageSize,
                                                 Long aId) {
        // 1.参数判断
        if(pageNum == null || pageSize == null || aId == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.文章判断
        Article article = articleDao.selectById(aId);
        if(article == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);

        // 3.文章作者判断
        User user = userDao.selectById(article.getUserId());
        if(user == null)
            throw new ServiceException(ResultCode.USER_NOT_EXIST);

        // 4.分页查询指定文章下所有的一级评论（含作者与二级评论s）
        IPage<CommentExtend> page = new Page<>(pageNum, pageSize);
        log.info("即将执行 queryByArticleId ... aId: " + aId);
        commentDao.queryByArticleId(page, aId);
        log.info("执行完成，commentExtends: " + page);

        return page;
    }

    @Override
    public IPage<CommentExtend> query(CommentQueryParam param) {
        // 1.参数判断
        Integer pageNum = param.getPageNum();
        Integer pageSize = param.getPageSize();
        if(param == null || pageNum == null || pageSize == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.分页+条件查询
        IPage<CommentExtend> page =
                new Page<>(pageNum, pageSize);
        commentDao.query(page, param.getKeyword(),
                param.getUserId(), param.getArticleId(),
                param.getStartTime(), param.getEndTime());

        return page;
    }
}
