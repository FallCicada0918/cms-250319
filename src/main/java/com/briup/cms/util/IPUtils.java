package com.briup.cms.util;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/18:41
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import com.briup.cms.bean.Ip;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;

/**
 * @Description ip工具类用于ip的获取与解析
 */
@Slf4j
public class IPUtils {
    // IP地址查询
    public static final String IP_URL =
            "http://whois.pconline.com.cn/ipJson.jsp";
    // 未知地址
    public static final String UNKNOWN = "unknown";
    /**
     * 公共对外接口,获取ip对象
     *
     * @param request 请求
     * @return ip对象
     */
    public static Ip getIP(HttpServletRequest request) {
        return getIp(getIpAddress(request));
    }
    /**
     * 获取 IP地址
     * 使用 Nginx等反向代理软件， 则不能通过 request.getRemoteAddr()
     获取 IP地址
     * 如果使用了多级反向代理的话，X-Forwarded-For的值并不止一个，而是
     一串IP地址，
     * X-Forwarded-For中第一个非 unknown的有效IP字符串，则为真实IP地
     址
     *
     * @param request 请求对象
     * @return ip地址
     */
    private static String getIpAddress(HttpServletRequest
                                               request) {
        String ip = null;

        //X-Forwarded-For：Squid 服务代理
        ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 ||
                UNKNOWN.equalsIgnoreCase(ip)) {
            //Proxy-Client-IP：apache 服务代理
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||
                UNKNOWN.equalsIgnoreCase(ip)) {
            //WL-Proxy-Client-IP：weblogic 服务代理
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 ||
                UNKNOWN.equalsIgnoreCase(ip)) {
            //HTTP_CLIENT_IP：有些代理服务器
            ip = request.getHeader("HTTP_CLIENT_IP");
        }
        //有些网络通过多层代理，那么获取到的ip就会有多个，一般都是通过逗号（,）分割开来，并且第一个ip为客户端的真实IP
        if (ip != null && ip.length() != 0) {
            ip = ip.split(",")[0];
        }
        if (ip == null || ip.length() == 0 ||
                UNKNOWN.equalsIgnoreCase(ip)) {
            //X-Real-IP：nginx服务代理
            ip = request.getHeader("X-Real-IP");
        }
        if (ip == null || ip.length() == 0 ||
                UNKNOWN.equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // return "0:0:0:0:0:0:0:1".equals(ip) ? "127.0.0.1" :ip;
        return ip;
    }
    /**
     * 根据IP地址,解析该IP,获取基础信息
     * 调用sendGet()方法 解析ip地址
     */
    private static Ip getIp(String ipAddress) {
        Ip ip = new Ip();
        try {
            String rspStr = sendGet("ip=" + ipAddress +
                    "&json=true");
            if (!StringUtils.hasText(rspStr)) {
                log.error("获取地理位置异常 {}", ipAddress);
                ip.setIp(UNKNOWN);
                return ip;
            }
            Gson gson = new Gson();
            ip = gson.fromJson(rspStr, Ip.class);
            return ip;
        } catch (Exception e) {
            log.error("获取地理位置异常 {}", ip);
        }
        return ip;
    }
    //返回格式
    /*
    {
        ip: "58.63.47.115",
        pro: "广东省",
        proCode: "440000",
        city: "广州市",
        cityCode: "440100",
        region: "天河区",
        regionCode: "440106",
        addr: "广东省广州市天河区 电信",
        regionNames: "",
        err: ""
    }
     */
    private static String sendGet(String param) {
        StringBuilder result = new StringBuilder();
        BufferedReader in = null;
        try {
            String urlNameString = IPUtils.IP_URL + "?" + param;
            log.info("sendGet - {}", urlNameString);
            URL realUrl = new URL(urlNameString);
            //连接设置
            URLConnection connection = realUrl.openConnection();
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep Alive");
                    connection.setRequestProperty("user-agent",
                            "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            //发送连接
            connection.connect();
            //获取解析后的返回的响应数据
            in = new BufferedReader(new
                    InputStreamReader(connection.getInputStream(), "GBK"));
            String line;
            while ((line = in.readLine()) != null) {
                //拼接结果
                result.append(line);
            }
            log.info("recv - {}", result);
        } catch (ConnectException e) {
            log.error("调用HttpUtils.sendGet ConnectException, url=" + IPUtils.IP_URL + ",param=" + param, e);
        } catch (SocketTimeoutException e) {
            log.error("调用HttpUtils.sendGet SocketTimeoutException, url=" + IPUtils.IP_URL + ",param=" +
                    param, e);
        } catch (IOException e) {
            log.error("调用HttpUtils.sendGet IOException, url=" +
                    IPUtils.IP_URL + ",param=" + param, e);
        } catch (Exception e) {
            log.error("调用HttpsUtil.sendGet Exception, url=" +
                    IPUtils.IP_URL + ",param=" + param, e);
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception ex) {
                log.error("调用in.close Exception, url=" +
                        IPUtils.IP_URL + ",param=" + param, ex);
            }
        }
        return result.toString();
    }
}
