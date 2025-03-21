package com.briup.cms.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import java.time.LocalDateTime;
import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * <p>
 * 
 * </p>
 *
 * @author briup
 * @since 2025-03-19
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("cms_log")
public class Log implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 编号
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 访问用户账号
     */
    private String username;

    /**
     * 接口描述信息
     */
    private String businessName;

    /**
     * 请求的地址
     */
    private String requestUrl;

    /**
     * 请求的方式，get post delete put
     */
    private String requestMethod;

    /**
     * ip
     */
    private String ip;

    /**
     * ip来源
     */
    private String source;

    /**
     * 请求接口耗时
     */
    /**
     * 上传时间，GMT指格林尼治所在地标准时间，＋8：00就是东八区的时间，即北京时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private Long spendTime;

    /**
     * 创建时间
     */
    /**
     * 上传时间，GMT指格林尼治所在地标准时间，＋8：00就是东八区的时间，即北京时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime createTime;

    /**
     * 请求的参数
     */
    private String paramsJson;

    /**
     * 响应参数
     */
    private String resultJson;


}
