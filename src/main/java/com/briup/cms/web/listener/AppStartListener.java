package com.briup.cms.web.listener;

import com.briup.cms.bean.Article;
import com.briup.cms.bean.User;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.dao.UserDao;
import com.briup.cms.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

//当前项目启动，系统自动调用该监听器对象的onApplicationEvent方法
//将所有文章id-阅读量写入redis服务器中
@Slf4j
@Component
public class AppStartListener
        implements ApplicationListener<ApplicationReadyEvent> {
    @Autowired
    private ArticleDao articleDao;
    @Autowired
    private UserDao userDao;
    @Autowired
    private RedisUtil redisUtil;

    private final String REDIS_KEY = "Article_Read_Num";

    //当项目启动，先删除redis中的key，然后再将所有id-readNum添加到redis服务器中
    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
       log.info("应用程序已启动！");

        //先删除redis中的key
        Map<Object,Object> map = redisUtil.getHash(REDIS_KEY);
        log.info("REDIS_KEY map: " + map);
        if(map != null && !map.isEmpty()) {
            log.info("删除 REDIS_KEY");
            redisUtil.del(REDIS_KEY);
        }

        //查询所有的文章，将其id-readNum写入redis中
        List<Article> records =
                articleDao.selectList(null);
        for (Article art : records) {
            Long userId = art.getUserId();
            User user = userDao.selectById(userId);
            //如果用户已经被删除或状态为禁用，则文章不可见
            if (user == null || "禁用".equals(user.getStatus()))
                continue;

            //往redis中添加key-value(文章id-浏览量)
            redisUtil.hset(REDIS_KEY, art.getId().toString(), art.getReadNum());
        }

        log.info("Redis阅读量初始化完毕!");
    }
}