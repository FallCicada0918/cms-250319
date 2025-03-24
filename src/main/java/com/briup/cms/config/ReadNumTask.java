package com.briup.cms.config;

import com.briup.cms.bean.Article;
import com.briup.cms.dao.ArticleDao;
import com.briup.cms.util.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

@Slf4j
@Component
public class ReadNumTask {
    @Autowired
    private ArticleDao articleDao;

    @Autowired
    private RedisUtil redisUtil;

    //redis当中阅读量的key
    private final String REDIS_KEY = "Article_Read_Num";

    // 设置每5分钟更新redis数据到mysql数据库中
    @Scheduled(cron = "0 0/5 * * * ?")
    public void saveReadNum(){
        //获取
        try {
            //从redis中获取所有文章及其阅读量
            Map<Object, Object> readNumMap = redisUtil.getHash(REDIS_KEY);
            //遍历map集合，逐项刷新到数据库文章表中
            Set<Map.Entry<Object, Object>> entries = readNumMap.entrySet();
            for (Map.Entry<Object, Object> entry : entries) {
                //获取key（文章id）
                Long articleId = Long.valueOf(entry.getKey().toString());
                //获取value（阅读量）
                Integer readNum = Integer.valueOf(entry.getValue().toString());

                //更新数据库中文章的阅读量
                Article article = new Article();
                article.setId(articleId);
                article.setReadNum(readNum);
                articleDao.updateById(article);
            }
            log.info("阅读量更新入库完毕!");
        }catch (Exception e) {
            log.info("阅读量入库失败，原因为：" + e.getMessage());
        }
    }
}
