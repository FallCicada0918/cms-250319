package com.briup.cms.web.interceptor;

import com.briup.cms.exception.ServiceException;
import com.briup.cms.util.JwtUtil;
import com.briup.cms.util.ResultCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author shaoyb
 * @program: 230831-cms
 * @description TODO
 * @create 2023/8/31 19:17
 **/
@Slf4j
public class JwtInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object object) {
        // 如果不是映射到方法直接通过
        if(!(object instanceof HandlerMethod)){
            return true;
        }

        // 从 HTTP请求头中取出 token
        String token = httpServletRequest.getHeader("Authorization");
        if (token == null) {
            throw new RuntimeException("无token，请重新登录");
        }

        // 验证 token
        try {
            //解析JWT
            Claims claims = JwtUtil.parseJWT(token);
            String id = String.valueOf(claims.get("userId"));
            //专门为 /auth/user/info 提供服务
            //给请求对象设置属性值，后续映射方法中可使用@RequestAttribute("userId")获取属性值
            httpServletRequest.setAttribute("userId",id);
        }catch (ExpiredJwtException e){
            //登录到期
            throw new RuntimeException("登录到期");
        }catch (MalformedJwtException e){
            //令牌失效
            throw new RuntimeException("令牌失效");
        }catch (Exception e){
            log.error(e.getMessage());
            //服务器内部错误
            throw new ServiceException(ResultCode.SYSTEM_INNER_ERROR);
        }

        return true;
    }
}
