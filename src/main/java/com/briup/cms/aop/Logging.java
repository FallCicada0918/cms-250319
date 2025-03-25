package com.briup.cms.aop;

/*
 * @Description: 
 * @Author:FallCicada
 * @Date: 2025/03/25/18:38
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用法： 当web层中方法需要提供日志记录功能，只需要在该方法添加logging注解
 即可
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Logging {
    /**
     * 日志描述信息,可用于描述接口的用途
     */
    String value() default "";
}
