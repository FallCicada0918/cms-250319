package com.briup.cms;

import com.briup.cms.util.MD5Utils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/20/15:15
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */
@SpringBootTest
public class MD5Test {

    MD5Utils md5Utils = new MD5Utils();
    @Test
    public void testMD5() {
        String password = "briup";
        String md5 = md5Utils.MD5(password);
        System.out.println(md5);

    }

    @Test
    public void testMD5_2() {
        String password = "5fa4d6fc78072f42e0b9817d310bcd35";
        String md5 = md5Utils.JM(password);
        System.out.println(md5);
    }

}
