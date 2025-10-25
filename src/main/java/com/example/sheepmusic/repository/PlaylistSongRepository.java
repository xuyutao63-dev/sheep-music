package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.PlaylistSong;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 歌单-歌曲关联 Repository
 */
@Repository
public interface PlaylistSongRepository extends JpaRepository<PlaylistSong, Long> {
    
    /**
     * 查询歌单的所有歌曲（按添加时间倒序）
     */
    List<PlaylistSong> findByPlaylistIdOrderByAddTimeDesc(Long playlistId);
    
    /**
     * 查询歌单的所有歌曲（按排序字段）
     */
    List<PlaylistSong> findByPlaylistIdOrderBySortOrderAsc(Long playlistId);
    
    /**
     * 分页查询歌单的歌曲
     */
    Page<PlaylistSong> findByPlaylistIdOrderByAddTimeDesc(Long playlistId, Pageable pageable);
    
    /**
     * 查询特定歌单-歌曲关联
     */
    Optional<PlaylistSong> findByPlaylistIdAndSongId(Long playlistId, Long songId);
    
    /**
     * 检查歌单中是否已包含某首歌
     */
    boolean existsByPlaylistIdAndSongId(Long playlistId, Long songId);
    
    /**
     * 统计歌单的歌曲数量
     */
    long countByPlaylistId(Long playlistId);
    
    /**
     * 删除歌单的所有歌曲（使用原生SQL，避免JPA状态冲突）
     */
    @Modifying
    @Transactional
    @Query(value = "DELETE FROM tb_playlist_song WHERE playlist_id = :playlistId", nativeQuery = true)
    void deleteByPlaylistId(@Param("playlistId") Long playlistId);
    
    /**
     * 删除歌单中的某首歌
     */
    @Modifying
    @Transactional
    void deleteByPlaylistIdAndSongId(Long playlistId, Long songId);
    
    /**
     * 批量插入歌曲到歌单
     */
    @Modifying
    @Query(value = "INSERT INTO tb_playlist_song (playlist_id, song_id, add_time, sort_order) " +
                   "VALUES (:playlistId, :songId, NOW(), :sortOrder)", 
           nativeQuery = true)
    void insertSong(@Param("playlistId") Long playlistId, 
                    @Param("songId") Long songId, 
                    @Param("sortOrder") Integer sortOrder);
}

