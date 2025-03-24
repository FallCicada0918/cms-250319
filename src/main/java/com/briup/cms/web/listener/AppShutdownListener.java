package com.briup.cms.web.listener;

import com.briup.cms.config.ReadNumTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class AppShutdownListener implements ApplicationListener<ContextClosedEvent> {

    @Autowired
    private ReadNumTask readNumTask;

    @Override
    public void onApplicationEvent(ContextClosedEvent event) {
        // 在这里执行关闭前的操作
        log.info("Application is shutting down...");
        // 关闭前刷新redis中阅读量到数据库
        readNumTask.saveReadNum();
    }
}
