package com.example.sheepmusic.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 用户实体类
 */
@Data
@Entity
@Table(name = "tb_user")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户名（登录账号）
     */
    @Column(unique = true, nullable = false, length = 50)
    private String username;
    
    /**
     * 密码（加密后）
     */
    @Column(nullable = false)
    private String password;
    
    /**
     * 昵称
     */
    @Column(length = 50)
    private String nickname;
    
    /**
     * 头像URL
     */
    @Column(length = 500)
    private String avatar;
    
    /**
     * 邮箱
     */
    @Column(length = 100)
    private String email;
    
    /**
     * 手机号
     */
    @Column(length = 20)
    private String phone;
    
    /**
     * 性别：0-未知，1-男，2-女
     */
    private Integer gender;
    
    /**
     * 个性签名
     */
    @Column(length = 200)
    private String signature;
    
    /**
     * 用户角色：user-普通用户，admin-管理员
     */
    @Column(length = 20, nullable = false)
    private String role = "user";
    
    /**
     * 账号状态：0-禁用，1-正常
     */
    @Column(nullable = false)
    private Integer status = 1;
    
    /**
     * 创建时间
     */
    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createTime;
    
    /**
     * 更新时间
     */
    @UpdateTimestamp
    private LocalDateTime updateTime;
}

