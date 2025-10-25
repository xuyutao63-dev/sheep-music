package com.example.sheepmusic.service;

import com.example.sheepmusic.entity.Favorite;
import com.example.sheepmusic.repository.FavoriteRepository;
import com.example.sheepmusic.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 收藏服务类
 */
@Service
public class FavoriteService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    /**
     * 添加收藏
     */
    @Transactional
    public Favorite addFavorite(Long userId, Long songId) {
        // 验证歌曲是否存在
        if (!songRepository.existsById(songId)) {
            throw new RuntimeException("歌曲不存在");
        }
        
        // 检查是否已经收藏
        if (favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            throw new RuntimeException("已经收藏过该歌曲");
        }
        
        // 创建收藏
        Favorite favorite = new Favorite();
        favorite.setUserId(userId);
        favorite.setSongId(songId);
        
        return favoriteRepository.save(favorite);
    }
    
    /**
     * 取消收藏
     */
    @Transactional
    public void removeFavorite(Long userId, Long songId) {
        favoriteRepository.deleteByUserIdAndSongId(userId, songId);
    }
    
    /**
     * 切换收藏状态（收藏/取消收藏）
     */
    @Transactional
    public boolean toggleFavorite(Long userId, Long songId) {
        if (favoriteRepository.existsByUserIdAndSongId(userId, songId)) {
            // 已收藏，取消收藏
            favoriteRepository.deleteByUserIdAndSongId(userId, songId);
            return false;
        } else {
            // 未收藏，添加收藏
            addFavorite(userId, songId);
            return true;
        }
    }
    
    /**
     * 检查是否已收藏
     */
    public boolean isFavorite(Long userId, Long songId) {
        return favoriteRepository.existsByUserIdAndSongId(userId, songId);
    }
    
    /**
     * 获取用户的收藏列表（分页）
     */
    public Page<Favorite> getUserFavorites(Long userId, Pageable pageable) {
        return favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 获取用户的收藏列表（不分页）
     */
    public List<Favorite> getUserFavorites(Long userId) {
        return favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    /**
     * 统计用户收藏数量
     */
    public long countUserFavorites(Long userId) {
        return favoriteRepository.countByUserId(userId);
    }
    
    /**
     * 批量检查收藏状态
     * 返回 Map<歌曲ID, 是否收藏>
     */
    public Map<Long, Boolean> batchCheckFavorites(Long userId, List<Long> songIds) {
        List<Long> favoriteSongIds = favoriteRepository.findFavoriteSongIdsByUserIdAndSongIds(userId, songIds);
        
        // 使用 distinct() 去重，避免 "Duplicate key" 异常
        return songIds.stream()
                .distinct()
                .collect(Collectors.toMap(
                        songId -> songId,
                        favoriteSongIds::contains
                ));
    }
}

