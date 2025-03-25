package com.briup.cms.util;

/*
 * @Description:
 * @Author:FallCicada
 * @Date: 2025/03/25/19:09
 * @LastEditors: 86138
 * @Slogan: 無限進步
 */

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.briup.cms.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Description bean拷贝工具类
 */
@Slf4j
public class BeanCopyUtils {
    /**
     * @param source
    源对象
     * @param targetClass 拷贝的目标类型
     * @param <T>
    泛型
     * @return 目标类的对象
     */
    public static <T> T copyBean(Object source, Class<T>
            targetClass) {
        T result = null;
        try {
            //创建目标类对象
            result = targetClass.newInstance();
            //使用Spring中的 BeanUtils工具类进行bean拷贝
            BeanUtils.copyProperties(source, result);
        } catch (Exception e) {
            log.error("拷贝失败！错误信息：{}", e.getMessage());
            throw new
                    ServiceException(ResultCode.SYSTEM_INNER_ERROR);
        }
        return result;
    }
    /**
     *
     * @param page 原分页对象
     * @param tClass 目标数据类型对象
     * @param <R> 源数据的类型
     * @param <T> 目标数据类型
    昆山杰普软件科技有限公司 No. 21/33
     * @return IPage<T> 新分页对象
     */
    public static <R, T> IPage<T> copyPage(IPage<R> page,
                                           Class<T> tClass) {
        //创建目标分页对象
        IPage<T> targetPage = new Page<>(page.getCurrent(),
                page.getSize());
        //将原分页对象内容,拷贝到目标对象中,提前将数据取出,为了节约资源, 可以先不拷贝[分页数据]
        List<R> records = page.getRecords();
        page.setRecords(null);
        BeanUtils.copyProperties(page, targetPage);
        //拷贝分页数据
        List<T> list = copyBeanList(records, tClass);
        targetPage.setRecords(list);
        return targetPage;
    }
    /**
     * list拷贝
     *
     * @param list        源数据所在的集合
     * @param targetClass 拷贝的目标类型
     * @param <R>         源数据的类型
     * @param <T>         目标类型
     * @return 拷贝后将数据存放于集合返回
     */
    public static <R, T> List<T> copyBeanList(List<R> list,
                                              Class<T> targetClass) {
        return list.stream()
                .map(o -> copyBean(o, targetClass))
                .collect(Collectors.toList());
    }
}