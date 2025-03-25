package com.briup.cms.aop;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/18:39
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import com.briup.cms.bean.Ip;
import com.briup.cms.bean.Log;
import com.briup.cms.dao.LogDao;
import com.briup.cms.util.IPUtils;
import com.briup.cms.util.JwtUtil;
import com.google.gson.Gson;
import io.jsonwebtoken.Claims;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @Description 日志切面类
 */
@Aspect
@Component
@Slf4j
public class LogAspect {
    @Autowired
    private LogDao logDao;
    @Autowired
    private Gson gson;
    //定义切入点: 当执行Controller包下的方法 并且方法上添加了日志注解 需要使用切面增强
    @Pointcut("execution(* com.briup.cms.web.controller.*.*(..)) && @annotation(com.briup.cms.aop.Logging)")
    public void pointcut() {
    }
    @SneakyThrows //lombok异常处理注解
    @Around("pointcut()")
    public Object around(ProceedingJoinPoint pjp) {
        //设置处理请求的开始时间
        Long start = System.currentTimeMillis();
        //日志对象
        Log sysLog = new Log();
        //获取接口信息,即接口的用途描述
        MethodSignature signature = (MethodSignature)
                pjp.getSignature();
        Method method = signature.getMethod();
        sysLog.setBusinessName(method.getAnnotation(Logging.class).value(
        ));
        log.info("接口功能:{}", sysLog.getBusinessName());
        //获取请求的参数(即方法的入参)
        sysLog.setParamsJson(gson.toJson(pjp.getArgs()));
        log.info("请求参数为:{}", sysLog.getParamsJson());
        //根据请求上下文 获取请求属性
        ServletRequestAttributes requestAttributes =
                (ServletRequestAttributes)
                        RequestContextHolder.getRequestAttributes();
        //获取请求
        assert requestAttributes != null;
        HttpServletRequest request =
                requestAttributes.getRequest();
        //获取token,根据token拿到用户信息
        String token = request.getHeader("Authorization");
        //已经进入到处理请求阶段,可以不用校验token,因为在登录认证拦截时已经校验过了
        if (!StringUtils.hasText(token)) {
            throw new RuntimeException("token不存在");
        }
        Claims claims = JwtUtil.parseJWT(token);

        sysLog.setUsername(String.valueOf(claims.get("username")));
        log.info("当前请求的用户为:{}", sysLog.getUsername());
        //获取请求路径url

        sysLog.setRequestUrl(String.valueOf(request.getRequestURL()));
        log.info("请求路径为:{}", sysLog.getRequestUrl());
        //获取请求方式
        sysLog.setRequestMethod(request.getMethod());
        log.info("请求方式为:{}", sysLog.getRequestMethod());
        //获取ip详情
        Ip ip = IPUtils.getIP(request);
        sysLog.setIp(ip.getIp());
        log.info("发送请求的ip为:{}", sysLog.getIp());
        sysLog.setSource(ip.getAddr());
        log.info("发送请求的ip来源为:{}", sysLog.getSource());
        //执行请求的接口,获取到执行结果(统一响应结果)
        Object obj = pjp.proceed();
        //设置响应结果
        sysLog.setResultJson(gson.toJson(obj));
        log.info("响应结果为:{}", sysLog.getResultJson());
        //设置处理请求的结束时间
        Long end = System.currentTimeMillis();
        //设置处理请求耗时
        sysLog.setSpendTime(end - start);
        log.info("请求耗时为:{}", sysLog.getSpendTime());
        //将日志存入数据库中
        logDao.insert(sysLog);
        return obj;
    }
}
