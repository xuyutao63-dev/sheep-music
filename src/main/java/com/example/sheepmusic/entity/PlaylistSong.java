package com.example.sheepmusic.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * 歌单-歌曲关联实体类
 */
@Data
@Entity
@Table(name = "tb_playlist_song")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class PlaylistSong {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "playlist_id", nullable = false)
    private Long playlistId;
    
    @Column(name = "song_id", nullable = false)
    private Long songId;
    
    @Column(name = "add_time", updatable = false)
    private LocalDateTime addTime;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    // 关联歌曲
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({"playHistories", "hibernateLazyInitializer", "handler"})
    private Song song;
    
    @PrePersist
    protected void onCreate() {
        addTime = LocalDateTime.now();
    }
}

