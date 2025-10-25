package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.Playlist;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 歌单 Repository
 */
@Repository
public interface PlaylistRepository extends JpaRepository<Playlist, Long> {
    
    /**
     * 查询用户的歌单列表（按创建时间倒序）
     */
    Page<Playlist> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 查询用户的所有歌单
     */
    List<Playlist> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 统计用户的歌单数量
     */
    long countByUserId(Long userId);
    
    /**
     * 查询公开歌单（分页）
     */
    Page<Playlist> findByIsPublicTrueOrderByCreateTimeDesc(Pageable pageable);
    
    /**
     * 查询所有公开或非公开歌单（不分页）
     * @param isPublic true=公开，false=非公开
     * @return 歌单列表
     */
    List<Playlist> findByIsPublicOrderByCreateTimeDesc(boolean isPublic);
    
    /**
     * 按分类查询公开歌单
     */
    Page<Playlist> findByIsPublicTrueAndCategoryOrderByCreateTimeDesc(
        String category, 
        Pageable pageable
    );
    
    /**
     * 查询热门公开歌单（按播放量倒序）
     */
    Page<Playlist> findByIsPublicTrueOrderByPlayCountDesc(Pageable pageable);
    
    /**
     * 搜索公开歌单（按名称）
     */
    @Query("SELECT p FROM Playlist p WHERE p.isPublic = true AND p.name LIKE %:keyword% ORDER BY p.createTime DESC")
    Page<Playlist> searchPublicPlaylistsByName(@Param("keyword") String keyword, Pageable pageable);
    
    /**
     * 增加播放次数
     */
    @Modifying
    @Query("UPDATE Playlist p SET p.playCount = p.playCount + 1 WHERE p.id = :playlistId")
    void incrementPlayCount(@Param("playlistId") Long playlistId);
    
    /**
     * 增加收藏次数
     */
    @Modifying
    @Query("UPDATE Playlist p SET p.collectCount = p.collectCount + 1 WHERE p.id = :playlistId")
    void incrementCollectCount(@Param("playlistId") Long playlistId);
    
    /**
     * 减少收藏次数
     */
    @Modifying
    @Query("UPDATE Playlist p SET p.collectCount = p.collectCount - 1 WHERE p.id = :playlistId AND p.collectCount > 0")
    void decrementCollectCount(@Param("playlistId") Long playlistId);
    
    /**
     * 更新歌曲数量
     */
    @Modifying
    @Query("UPDATE Playlist p SET p.songCount = :count WHERE p.id = :playlistId")
    void updateSongCount(@Param("playlistId") Long playlistId, @Param("count") Integer count);
}

