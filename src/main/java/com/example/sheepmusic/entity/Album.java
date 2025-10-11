package com.example.sheepmusic.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 专辑实体类
 */
@Data
@Entity
@Table(name = "tb_album")
public class Album {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 专辑名称
     */
    @Column(nullable = false, length = 100)
    private String name;
    
    /**
     * 专辑封面URL
     */
    @Column(length = 500)
    private String cover;
    
    /**
     * 专辑简介
     */
    @Column(length = 1000)
    private String description;
    
    /**
     * 发行时间
     */
    private LocalDate releaseDate;
    
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

