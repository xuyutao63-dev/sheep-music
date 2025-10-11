package com.example.sheepmusic.entity;

import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 歌曲实体类
 */
@Data
@Entity
@Table(name = "tb_song")
public class Song {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    /**
     * 歌曲名称
     */
    @Column(nullable = false, length = 100)
    private String title;
    
    /**
     * 歌手ID
     */
    @Column(nullable = false)
    private Long artistId;
    
    /**
     * 歌手名称（冗余字段，方便查询）
     */
    @Column(length = 100)
    private String artistName;
    
    /**
     * 专辑ID（可为空）
     */
    private Long albumId;
    
    /**
     * 专辑名称（冗余字段）
     */
    @Column(length = 100)
    private String albumName;
    
    /**
     * 歌曲时长（秒）
     */
    private Integer duration;
    
    /**
     * 封面图片URL
     */
    @Column(length = 500)
    private String cover;
    
    /**
     * 音频文件URL
     */
    @Column(nullable = false, length = 500)
    private String url;
    
    /**
     * 歌词（纯文本格式，LRC格式）
     */
    @Column(columnDefinition = "TEXT")
    private String lyric;
    
    /**
     * 播放次数
     */
    @Column(nullable = false)
    private Long playCount = 0L;
    
    /**
     * 发行时间
     */
    private LocalDateTime releaseTime;
    
    /**
     * 歌曲状态：0-下架，1-上架
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

