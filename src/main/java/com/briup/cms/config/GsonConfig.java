package com.briup.cms.config;

import com.google.gson.Gson;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/18:40
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
@Configuration
public class GsonConfig {
    @Bean
    @ConditionalOnMissingBean
    public Gson gson() {
        return new Gson();
    }
}