package com.example.sheepmusic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌单实体类
 */
@Data
@Entity
@Table(name = "tb_playlist")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Playlist {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(columnDefinition = "TEXT")
    private String cover; // JSON数组，存储前4首歌的封面URL
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "is_public")
    private Boolean isPublic = false;
    
    @Column(length = 50)
    private String category;
    
    @Column(name = "play_count")
    private Long playCount = 0L;
    
    @Column(name = "collect_count")
    private Long collectCount = 0L;
    
    @Column(name = "song_count")
    private Integer songCount = 0;
    
    @Column(name = "create_time", updatable = false)
    private LocalDateTime createTime;
    
    @Column(name = "update_time")
    private LocalDateTime updateTime;
    
    // 关联创建者
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"password", "email", "phone", "hibernateLazyInitializer", "handler"})
    private User creator;
    
    // 注意：歌曲关联通过 PlaylistSong 实体管理，不使用 @ManyToMany
    // 避免与 PlaylistSong 实体产生冲突
    
    @PrePersist
    protected void onCreate() {
        createTime = LocalDateTime.now();
        updateTime = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updateTime = LocalDateTime.now();
    }
}

