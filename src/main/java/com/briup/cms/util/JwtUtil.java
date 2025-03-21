package com.briup.cms.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;
import java.util.Map;

public class JwtUtil {
    //签名秘钥
    private static String signKey = "briup";
    //过期时间(1天),一般30分钟
    private static Long expire = 60 * 60 * 24 * 1000L;
    /**
     * 生成JWT令牌
     * @param claims JWT第二部分负载 payload 中存储的内容
     * @return
     */
    public static String generateJwt(Map<String, Object> claims)
    {
        String jwt = Jwts.builder()
                //自定义信息（有效载荷）
                .addClaims(claims)
                //签名算法（头部）
                .signWith(SignatureAlgorithm.HS256, signKey)
                //过期时间
                .setExpiration(new
                        Date(System.currentTimeMillis() + expire))
                .compact();
        return jwt;
    }
    /**
     * 解析JWT令牌
     * @param jwt JWT令牌
     * @return JWT第二部分负载 payload 中存储的内容
     */
    public static Claims parseJWT(String jwt) {
        Claims claims = Jwts.parser()
                //指定签名秘钥
                .setSigningKey(signKey)
                //指定JWT令牌
                .parseClaimsJws(jwt)
                .getBody();
        return claims;
    }
    /**
     * 获取用户ID
     * @param token
     * @return
     */
    public static Long getUserId(String token) {
        Claims claims = parseJWT(token);
        String userIdStr = (String) claims.get("userId");
        long userId = Long.parseLong(userIdStr);
        //long userId = (Long) claims.get("userId");
        return userId;
    }
}