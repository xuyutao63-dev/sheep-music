package com.example.sheepmusic.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 歌手实体类
 */
@Data
@Entity
@Table(name = "tb_artist")
public class Artist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 歌手名称
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * 歌手头像URL
     */
    @Column(length = 1000)
    private String avatar;
    
    /**
     * 歌手简介
     */
    @Column(length = 1000)
    private String description;
    
    /**
     * 国家/地区
     */
    @Column(length = 50)
    private String region;
    
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

