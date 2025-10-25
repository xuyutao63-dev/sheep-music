package com.example.sheepmusic.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 播放历史实体类
 * 记录用户的歌曲播放历史
 */
@Data
@Entity
@Table(name = "tb_play_history", indexes = {
    @Index(name = "idx_user_play_time", columnList = "user_id,play_time")
})
public class PlayHistory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 用户ID
     */
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    /**
     * 歌曲ID
     */
    @Column(name = "song_id", nullable = false)
    private Long songId;
    
    /**
     * 播放时间
     */
    @CreationTimestamp
    @Column(name = "play_time", nullable = false, updatable = false)
    private LocalDateTime playTime;
    
    /**
     * 播放时长（秒），可选字段
     * 记录用户实际听了多久
     */
    @Column(name = "play_duration")
    private Integer playDuration;
    
    /**
     * 关联查询：获取歌曲详情
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"playHistories"}) // 忽略 Song 中的反向引用
    private Song song;
    
    /**
     * 关联查询：获取用户详情
     * 前端不需要用户信息，直接忽略序列化
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore // 完全忽略 user 字段的序列化，避免 Hibernate 代理问题
    private User user;
}

