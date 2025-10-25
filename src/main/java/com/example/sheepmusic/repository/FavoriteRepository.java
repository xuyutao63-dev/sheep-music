package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.Favorite;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * 收藏数据访问层
 */
@Repository
public interface FavoriteRepository extends JpaRepository<Favorite, Long> {
    
    /**
     * 查找用户的某个收藏
     */
    Optional<Favorite> findByUserIdAndSongId(Long userId, Long songId);
    
    /**
     * 检查用户是否收藏了某首歌
     */
    boolean existsByUserIdAndSongId(Long userId, Long songId);
    
    /**
     * 获取用户的所有收藏（分页）
     */
    Page<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId, Pageable pageable);
    
    /**
     * 获取用户的所有收藏（不分页）
     */
    List<Favorite> findByUserIdOrderByCreateTimeDesc(Long userId);
    
    /**
     * 统计用户收藏数量
     */
    long countByUserId(Long userId);
    
    /**
     * 删除用户的某个收藏
     */
    void deleteByUserIdAndSongId(Long userId, Long songId);
    
    /**
     * 批量检查用户是否收藏了多首歌曲
     */
    @Query("SELECT f.songId FROM Favorite f WHERE f.userId = :userId AND f.songId IN :songIds")
    List<Long> findFavoriteSongIdsByUserIdAndSongIds(@Param("userId") Long userId, @Param("songIds") List<Long> songIds);
}

