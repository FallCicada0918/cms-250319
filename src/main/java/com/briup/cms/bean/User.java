package com.briup.cms.bean;

import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.IdType;
import java.time.LocalDate;
import com.baomidou.mybatisplus.annotation.Version;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
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
@TableName("cms_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 用户id
     */
    @TableId(value = "id", type = IdType.ASSIGN_ID)
    private Long id;

    /**
     * 用户名称
     */
    private String username;

    /**
     * 用户密码
     */
    private String password;

    /**
     * 用户头像
     */
    private String avatar;

    /**
     * 性别
     */
    private String gender;

    /**
     * 用户邮箱
     */
    private String email;

    /**
     * 用户电话
     */
    private String phone;

    /**
     * 注册时间
     */
    /**
     * 上传时间，GMT指格林尼治所在地标准时间，＋8：00就是东八区的时间，即北京时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime registerTime;

    /**
     * 用户状态
     */
    private String status;

    /**
     * 生日
     */
    /**
     * 上传时间，GMT指格林尼治所在地标准时间，＋8：00就是东八区的时间，即北京时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd",timezone = "GMT+8")
    private LocalDate birthday;

    /**
     * 角色id
     */
    private Integer roleId;

    /**
     * 是否为会员
     */
    private Integer isVip;

    /**
     * 会员到期时间
     */
    /**
     * 上传时间，GMT指格林尼治所在地标准时间，＋8：00就是东八区的时间，即北京时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss",timezone = "GMT+8")
    private LocalDateTime expiresTime;

    /**
     * 用户删除状态
     */
    @TableLogic
    private Integer deleted;


}
