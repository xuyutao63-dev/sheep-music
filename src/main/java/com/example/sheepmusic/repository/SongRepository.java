package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.Song;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 歌曲数据访问层
 */
@Repository
public interface SongRepository extends JpaRepository<Song, Long> {
    
    /**
     * 根据歌手ID查找歌曲
     */
    List<Song> findByArtistId(Long artistId);
    
    /**
     * 根据专辑ID查找歌曲
     */
    List<Song> findByAlbumId(Long albumId);
    
    /**
     * 根据状态查找歌曲（分页）
     */
    Page<Song> findByStatus(Integer status, Pageable pageable);
    
    /**
     * 搜索歌曲（根据歌曲名或歌手名）
     */
    @Query("SELECT s FROM Song s WHERE s.title LIKE %:keyword% OR s.artistName LIKE %:keyword%")
    Page<Song> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 获取热门歌曲（按播放次数排序）
     */
    Page<Song> findByStatusOrderByPlayCountDesc(Integer status, Pageable pageable);
    
    /**
     * 获取最新歌曲（按创建时间排序）
     */
    Page<Song> findByStatusOrderByCreateTimeDesc(Integer status, Pageable pageable);
}

