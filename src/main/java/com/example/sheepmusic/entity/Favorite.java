package com.example.sheepmusic.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 收藏实体类
 */
@Data
@Entity
@Table(name = "tb_favorite", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "song_id"})
})
public class Favorite {
    
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
     * 歌曲信息（冗余，方便查询）
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    private Song song;
    
    /**
     * 收藏时间
     */
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
}

