package com.briup.cms;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.briup.cms.dao")
@SpringBootApplication
public class Cms250319Application {

    public static void main(String[] args) {
        SpringApplication.run(Cms250319Application.class, args);
    }

}
