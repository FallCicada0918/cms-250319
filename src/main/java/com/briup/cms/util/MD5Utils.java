package com.briup.cms.util;

import java.security.MessageDigest;

public class MD5Utils {
    // 加密算法
    public static String MD5(String inStr) {
        MessageDigest md5 = null;
        try {
            // 获取MD5加密算法实例
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        // 将字符串转换为字节数组
        byte[] byteArray = inStr.getBytes();
        // 使用MD5算法对字节数组进行加密
        byte[] md5Bytes = md5.digest(byteArray);
        // 将加密后的字节数组转换为十六进制字符串
        StringBuilder hexValue = new StringBuilder();
        for (byte md5Byte : md5Bytes) {
            int val = ((int) md5Byte) & 0xff;
            //1.如果val为0-f，则先往StringBuilder中加0
            if (val < 16)
                hexValue.append("0");
            //2.再将16进制数值加进入，得到: 01 0a 0f ...
            hexValue.append(Integer.toHexString(val));
        }
        // 返回16进制字符串，共计32位，128字节
        return hexValue.toString();
    }
    // 可逆的加密算法
    public static String KL(String inStr) {
        // String s = new String(inStr);
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        return new String(a);
    }
    // 加密后解密
    public static String JM(String inStr) {
        char[] a = inStr.toCharArray();
        for (int i = 0; i < a.length; i++) {
            a[i] = (char) (a[i] ^ 't');
        }
        return new String(a);
    }
}
