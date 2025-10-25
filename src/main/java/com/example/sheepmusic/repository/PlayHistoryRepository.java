package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.PlayHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 播放历史数据访问层
 */
@Repository
public interface PlayHistoryRepository extends JpaRepository<PlayHistory, Long> {
    
    /**
     * 分页查询用户的播放历史（按播放时间倒序）
     */
    Page<PlayHistory> findByUserIdOrderByPlayTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 分页查询用户的播放历史（带歌曲详情，避免N+1查询）
     * 注意：Song.artists 已配置为 EAGER 加载，无需再 JOIN FETCH
     */
    @Query(value = "SELECT ph FROM PlayHistory ph " +
           "JOIN FETCH ph.song s " +
           "WHERE ph.userId = :userId " +
           "ORDER BY ph.playTime DESC",
           countQuery = "SELECT COUNT(ph) FROM PlayHistory ph WHERE ph.userId = :userId")
    Page<PlayHistory> findByUserIdWithSongDetails(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 分页查询用户的播放历史（去重，每首歌只显示最新的播放记录）
     * 使用原生 SQL 查询实现
     */
    @Query(value = "SELECT ph.* FROM tb_play_history ph " +
           "INNER JOIN (" +
           "    SELECT song_id, MAX(play_time) as max_time " +
           "    FROM tb_play_history " +
           "    WHERE user_id = :userId " +
           "    GROUP BY song_id" +
           ") latest ON ph.song_id = latest.song_id AND ph.play_time = latest.max_time " +
           "WHERE ph.user_id = :userId " +
           "ORDER BY ph.play_time DESC",
           countQuery = "SELECT COUNT(DISTINCT song_id) FROM tb_play_history WHERE user_id = :userId",
           nativeQuery = true)
    Page<PlayHistory> findLatestByUserIdGroupBySong(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计用户的播放历史总数
     */
    long countByUserId(Long userId);
    
    /**
     * 统计用户播放过的不同歌曲数量
     */
    @Query("SELECT COUNT(DISTINCT ph.songId) FROM PlayHistory ph WHERE ph.userId = :userId")
    long countDistinctSongsByUserId(@Param("userId") Long userId);
    
    /**
     * 删除用户的所有播放历史
     */
    void deleteByUserId(Long userId);
    
    /**
     * 删除指定时间之前的播放历史（清理旧数据）
     */
    void deleteByPlayTimeBefore(LocalDateTime dateTime);
    
    /**
     * 查询用户最近播放的N首歌曲
     */
    @Query("SELECT ph FROM PlayHistory ph " +
           "WHERE ph.userId = :userId " +
           "ORDER BY ph.playTime DESC")
    List<PlayHistory> findRecentByUserId(@Param("userId") Long userId, Pageable pageable);
    
    /**
     * 统计歌曲的播放次数
     */
    long countBySongId(Long songId);
    
    /**
     * 统计用户播放某首歌的次数
     */
    long countByUserIdAndSongId(Long userId, Long songId);
    
    /**
     * 查询用户在指定时间范围内的播放历史
     */
    @Query("SELECT ph FROM PlayHistory ph " +
           "WHERE ph.userId = :userId " +
           "AND ph.playTime BETWEEN :startTime AND :endTime " +
           "ORDER BY ph.playTime DESC")
    List<PlayHistory> findByUserIdAndTimeRange(
        @Param("userId") Long userId,
        @Param("startTime") LocalDateTime startTime,
        @Param("endTime") LocalDateTime endTime
    );
}

