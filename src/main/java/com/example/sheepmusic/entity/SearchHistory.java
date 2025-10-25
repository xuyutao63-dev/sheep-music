package com.example.sheepmusic.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 搜索历史实体
 */
@Data
@Entity
@Table(name = "search_history")
public class SearchHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 搜索关键词
     */
    @Column(name = "keyword", nullable = false, length = 100)
    private String keyword;
    
    /**
     * 搜索时间
     */
    @CreationTimestamp
    @Column(name = "search_time", nullable = false, updatable = false)
    private LocalDateTime searchTime;
}

