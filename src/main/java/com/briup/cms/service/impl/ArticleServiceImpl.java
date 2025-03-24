package com.briup.cms.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.bean.Article;
import com.briup.cms.bean.Category;
import com.briup.cms.bean.Comment;
import com.briup.cms.bean.User;
import com.briup.cms.bean.extend.ArticleExtend;
import com.briup.cms.bean.vo.ArticleParam;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.dao.CategoryDao;
import com.briup.cms.dao.CommentDao;
import com.briup.cms.dao.UserDao;
import com.briup.cms.exception.ServiceException;
import com.briup.cms.service.IArticleService;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.ResultCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class ArticleServiceImpl implements IArticleService {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private CategoryDao categoryDao;
    //原生的请求对象
    @Autowired
    private HttpServletRequest request;
    //注入一级评论dao层对象
    @Autowired
    private CommentDao commentDao;

    @Override
    public void saveOrUpdate(Article article) {
        // 文章状态判断：如果文章已经发表，普通用户不能再修改其标题、内容等信息

        // 1.参数判断
        if(article == null) {
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);
        }

        // 2.用户判断：如果用户不存在，或状态为“禁用”，抛异常
        String token = request.getHeader("Authorization");
        Long userId = JwtUtil.getUserId(token);
        log.info("userId: {}", userId);
        User loginUser = userDao.selectById(userId);
        if(loginUser == null)
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        if("禁用".equals(loginUser.getStatus()))
            throw new ServiceException(ResultCode.USER_ACCOUNT_FORBIDDEN);

        article.setUserId(userId);

        // 3.栏目判断
        Integer categoryId = article.getCategoryId();
        //3.1 如果新增操作，则栏目id必须存在
        if(article.getId() == null && categoryId == null) {
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
        }
        //3.2 栏目不存在，或栏目不是2级栏目，则抛异常
        if(categoryId != null) {
            Category category = categoryDao.selectById(categoryId);
            if (category == null || category.getParentId() == null)
                throw new ServiceException(ResultCode.CATEGORY_NOT_EXIST);
        }
        //获取当前登录用户角色及文章id
        Integer roleId = loginUser.getRoleId();
        log.info("roleId: {}", roleId);
        Long id = article.getId();
        if(id == null) {
            // 4.新增操作
            // 准备文章发表时间
            article.setPublishTime(LocalDateTime.now());

            // 根据当前登录用户角色，设置文章审核状态
            if(roleId == 3) {
                article.setStatus("未审核");
            }else {
                article.setStatus("审核通过");
            }

            // 数据库插入文章
            articleDao.insert(article);
        }else {
            // 5.修改操作
            // 判断文章id是否存在
            Article dbArticle = articleDao.selectById(id);
            if(dbArticle == null)
                throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);

            //角色及文章状态校验: 如果普通用户 修改 审核已经通过的文章，直接报错
            if(roleId == 3 && "审核通过".equals(dbArticle.getStatus()))
                throw new ServiceException(ResultCode.PARAM_IS_INVALID);

            // 更新文章信息
            articleDao.updateById(article);
        }
    }

    @Override
    public void reviewArticle(Long id, String status) {
        // 1.参数判断
        if(id == null || status == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.文章必须存在
        if(articleDao.selectById(id) == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);

        // 3.修改文章审核状态
        Article article = new Article();
        article.setId(id);
        article.setStatus(status);
        articleDao.updateById(article);
    }

    @Override
    public void deleteInBatch(List<Long> ids) {
        // 1.参数判断
        if(ids == null || ids.size() == 0)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.执行批量删除，只要成功删除1条就算成功，删除0条算失败
        int num = articleDao.deleteBatchIds(ids);
        log.info("delete num: {}", num);

        if(num == 0)
            throw new ServiceException(ResultCode.PARAM_IS_INVALID);
    }

    /* 该接口专门为前端提供：查询指定的文章（包含3条1级评论，还有作者）
     - 如果文章的发布者已被删除或禁用，则该文章不能查看
     - 如果当前登录用户非管理员、非发布者，且文章的状态非"审核通过"，则该文章不能查看
     - 如果当前登录用户非管理员、非发布者、非会员，且文章为收费文章，则不能查看该文章
 */
    @Override
    public ArticleExtend queryByIdWithCommentsAndUser(Long id) {
        // 1.参数判断
        if (id == null)
            throw new ServiceException(ResultCode.PARAM_IS_BLANK);

        // 2.判断文章是否存在
        Article article = articleDao.selectById(id);
        if (article == null)
            throw new ServiceException(ResultCode.ARTICLE_NOT_EXIST);

        // 3.校验文章发布者是否有效
        long ownerId = article.getUserId();
        User owner = userDao.selectById(ownerId);
        if(owner == null || "禁用".equals(owner.getStatus())) {
            throw new ServiceException(ResultCode.USER_NOT_EXIST);
        }

        // 4.如果当前登录用户非管理员、非发布者，且文章的状态非"审核通过"，则该文章不能查看
        // 获取当前登录用户及角色
        String token = request.getHeader("Authorization");
        long loginId = JwtUtil.getUserId(token);
        User loginUser = userDao.selectById(loginId);
        Integer roleId = loginUser.getRoleId();
        log.info("ownerId: {}", ownerId);
        log.info("loginId: {}", loginId);
        // 当前登录用户 非管理员、非文章发布者
        if(roleId == 3 && ownerId != loginId) {
            // 如果文章状态非"审核通过"，则不能查看该文章
            if(!"审核通过".equals(article.getStatus())) {
                throw new ServiceException(ResultCode.PERMISSION_NO_ACCESS);
            }

            // 当前登录用户非会员，且文章为收费文章，则不能查看该文章
            if(loginUser.getIsVip() != 1 && article.getCharged() == 1) {
                throw new ServiceException(ResultCode.PERMISSION_NO_ACCESS);
            }
        }

        // 5.复制文章对象属性 到 扩展类对象中
        ArticleExtend articleExtend = new ArticleExtend();
        BeanUtils.copyProperties(article, articleExtend);

        // 6.补充作者(屏蔽密码) 到 扩展类对象中
        owner.setPassword(null);
        articleExtend.setAuthor(owner);

        // 7.根据文章id查询一级评论，按发表时间倒序，取最近3条
        LambdaQueryWrapper<Comment> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Comment::getArticleId, id)
                .orderByDesc(Comment::getPublishTime)
                .last("limit 3");
        List<Comment> comments = commentDao.selectList(wrapper);
        // 填入查询到的一级评论到扩展类对象中
        if(comments != null && comments.size() > 0)
            articleExtend.setComments(comments);

        //8.浏览量自增【拥有者浏览自己的文章 不增加 浏览量】
        if(ownerId != loginId) {
            Integer readNum = article.getReadNum();
            readNum++;

            //更新数据库中 浏览量
            Article newArticle = new Article();
            newArticle.setId(id);
            newArticle.setReadNum(readNum);
            articleDao.updateById(newArticle);

            //修改响应的文章对象中的浏览量
            articleExtend.setReadNum(readNum);
        }
        //Redis优化阅读量代码
//        if(ownerId != loginId) {
//            //对redis中指定id文章浏览量进行自增
//            Integer readNum =
//                    redisUtil.increment(REDIS_KEY, article.getId().toString());
//            //修改文章的最新浏览量
//            articleExtend.setReadNum(readNum);
//            //log.info("浏览量: " + articleExtend.getReadNum());
//        }

        return articleExtend;
    }

    @Override
    public IPage<ArticleExtend> query(ArticleParam articleParam) {
        //1.单表查询文章信息
        IPage<Article> page =
                new Page<>(articleParam.getPageNum(),articleParam.getPageSize());

        //准备查询条件对象
        LambdaQueryWrapper<Article> queryWrapper =
                new LambdaQueryWrapper<>();
        //title模糊匹配
        queryWrapper.like(StringUtils.hasText(articleParam.getTitle()),
                Article::getTitle, articleParam.getTitle());
        queryWrapper.eq(articleParam.getCategoryId() != null,
                Article::getCategoryId, articleParam.getCategoryId());
        // 针对String类型的参数，必须使用StringUtils.hasText判空
        queryWrapper.eq(StringUtils.hasText(articleParam.getStatus()),
                Article::getStatus, articleParam.getStatus());
        queryWrapper.eq(articleParam.getUserId() != null,
                Article::getUserId, articleParam.getUserId());
        queryWrapper.eq(articleParam.getCharged() != null,
                Article::getCharged, articleParam.getCharged());
        //startTime及endTime范围匹配
        queryWrapper.gt(articleParam.getStartTime() != null,
                Article::getPublishTime, articleParam.getStartTime());
        queryWrapper.lt(articleParam.getEndTime() != null,
                Article::getPublishTime, articleParam.getEndTime());

        //执行分页+条件查询
        articleDao.selectPage(page,queryWrapper);

        //2.额外查询作者信息，封装得到ArticleExtend对象
        List<Article> articleList = page.getRecords();
        ArrayList<ArticleExtend> list = new ArrayList<>();
        for(Article article : articleList) {
            //1.先获取作者信息，添加到articleExtend对象中
            //注意：如果作者已经删除或禁用，则该文章无效
            Long userId = article.getUserId();
            User user = userDao.selectById(userId);
            if(user == null || "禁用".equals(user.getStatus()))
                continue;

            //2.拷贝article对象属性到articleExtend对象中
            ArticleExtend articleExtend = new ArticleExtend();
            BeanUtils.copyProperties(article, articleExtend);

            //3.补充作者信息
            //log.info("author: {}", user);
            //额外注释密码，不能返回给前端
            user.setPassword(null);
            articleExtend.setAuthor(user);

            //4.添加到集合中
            list.add(articleExtend);
        }

        //3.由IPage<Article> 封装得到 IPage<ArticleExtend>
        Page<ArticleExtend> pageInfo = new Page<>();
        pageInfo.setRecords(list);
        //获取 无效文章数量
        int count = articleList.size() - list.size();
        //设置 总条数（去除无效文章）
        pageInfo.setTotal(page.getTotal() - count);
        pageInfo.setCurrent(page.getCurrent());

//        log.info("page.getCurrent: {}", page.getCurrent());
//        log.info("page.getSize: {}", page.getSize());
//        log.info("page.getTotal: {}", page.getTotal());
//        log.info("list.size: {}", list.size());

        return pageInfo;
    }
}
