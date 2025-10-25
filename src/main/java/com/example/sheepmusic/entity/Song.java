package com.example.sheepmusic.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 歌曲实体类
 */
@Data
@Entity
@Table(name = "tb_song")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
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
     * 歌手列表（多对多关系）
     */
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
        name = "song_artist",
        joinColumns = @JoinColumn(name = "song_id"),
        inverseJoinColumns = @JoinColumn(name = "artist_id")
    )
    private List<Artist> artists = new ArrayList<>();
    
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
     * 歌曲类型/风格
     */
    @Column(length = 50)
    private String genre;
    
    /**
     * 歌曲语言
     */
    @Column(length = 20)
    private String language;
    
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

