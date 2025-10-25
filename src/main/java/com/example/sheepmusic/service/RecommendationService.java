package com.example.sheepmusic.service;

import com.example.sheepmusic.entity.*;
import com.example.sheepmusic.repository.FavoriteRepository;
import com.example.sheepmusic.repository.PlayHistoryRepository;
import com.example.sheepmusic.repository.PlaylistRepository;
import com.example.sheepmusic.repository.PlaylistSongRepository;
import com.example.sheepmusic.repository.SongRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 推荐系统服务 - 基于协同过滤算法
 */
@Service
public class RecommendationService {
    
    @Autowired
    private FavoriteRepository favoriteRepository;
    
    @Autowired
    private PlayHistoryRepository playHistoryRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private PlaylistSongRepository playlistSongRepository;
    
    /**
     * 获取用户个性化推荐歌曲（基于用户协同过滤）
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐歌曲列表
     */
    public List<Song> getPersonalizedSongs(Long userId, int limit) {
        // 1. 获取用户喜欢的歌曲（收藏 + 多次播放的歌曲）
        Set<Long> userLikedSongIds = getUserLikedSongs(userId);
        
        if (userLikedSongIds.isEmpty()) {
            // 冷启动：返回热门歌曲
            return getHotSongs(limit);
        }
        
        // 2. 找到有相似品味的用户（基于用户协同过滤）
        Map<Long, Double> similarUsers = findSimilarUsers(userId, userLikedSongIds);
        
        if (similarUsers.isEmpty()) {
            // 如果没有找到相似用户，返回热门歌曲
            return getHotSongs(limit);
        }
        
        // 3. 收集候选歌曲（相似用户喜欢的歌曲）
        Map<Long, Double> candidateSongs = new HashMap<>();
        
        for (Map.Entry<Long, Double> entry : similarUsers.entrySet()) {
            Long similarUserId = entry.getKey();
            Double similarity = entry.getValue();
            
            Set<Long> theirSongs = getUserLikedSongs(similarUserId);
            for (Long songId : theirSongs) {
                // 过滤掉用户已经喜欢的歌曲
                if (!userLikedSongIds.contains(songId)) {
                    // 使用相似度加权
                    candidateSongs.put(songId, 
                        candidateSongs.getOrDefault(songId, 0.0) + similarity);
                }
            }
        }
        
        // 4. 按推荐分数排序并获取Top N
        List<Long> recommendedSongIds = candidateSongs.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        // 5. 如果推荐结果不足，用热门歌曲补充
        if (recommendedSongIds.size() < limit) {
            List<Song> hotSongs = getHotSongs(limit - recommendedSongIds.size());
            List<Song> recommendations = new ArrayList<>();
            
            // 先添加推荐的歌曲
            if (!recommendedSongIds.isEmpty()) {
                recommendations.addAll(songRepository.findAllById(recommendedSongIds));
            }
            
            // 再补充热门歌曲（排除已推荐的和用户已喜欢的）
            for (Song song : hotSongs) {
                if (!recommendedSongIds.contains(song.getId()) && 
                    !userLikedSongIds.contains(song.getId())) {
                    recommendations.add(song);
                    if (recommendations.size() >= limit) break;
                }
            }
            
            return recommendations;
        }
        
        return songRepository.findAllById(recommendedSongIds);
    }
    
    /**
     * 获取相似歌曲（基于物品协同过滤）
     * 
     * @param songId 歌曲ID
     * @param limit 返回数量
     * @return 相似歌曲列表
     */
    public List<Song> getSimilarSongs(Long songId, int limit) {
        // 1. 获取收藏/播放过这首歌的所有用户
        Set<Long> usersWhoLikeThisSong = getUsersWhoLikeSong(songId);
        
        if (usersWhoLikeThisSong.isEmpty()) {
            return Collections.emptyList();
        }
        
        // 2. 计算其他歌曲与该歌曲的相似度（共同喜欢的用户数）
        Map<Long, Integer> coOccurrence = new HashMap<>();
        
        for (Long userId : usersWhoLikeThisSong) {
            Set<Long> userSongs = getUserLikedSongs(userId);
            for (Long otherSongId : userSongs) {
                if (!otherSongId.equals(songId)) {
                    coOccurrence.put(otherSongId, 
                        coOccurrence.getOrDefault(otherSongId, 0) + 1);
                }
            }
        }
        
        // 3. 计算余弦相似度并排序
        Map<Long, Double> similarity = new HashMap<>();
        int songUserCount = usersWhoLikeThisSong.size();
        
        for (Map.Entry<Long, Integer> entry : coOccurrence.entrySet()) {
            Long otherSongId = entry.getKey();
            int coCount = entry.getValue();
            int otherSongUserCount = getUsersWhoLikeSong(otherSongId).size();
            
            // 余弦相似度 = 共同用户数 / sqrt(歌曲1用户数 * 歌曲2用户数)
            double sim = coCount / Math.sqrt(songUserCount * otherSongUserCount);
            similarity.put(otherSongId, sim);
        }
        
        // 4. 按相似度排序并返回Top N
        List<Long> similarSongIds = similarity.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        return songRepository.findAllById(similarSongIds);
    }
    
    /**
     * 推荐歌单给用户
     * 基于歌单内容与用户兴趣的匹配度
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐歌单列表
     */
    public List<Playlist> getRecommendedPlaylists(Long userId, int limit) {
        // 1. 获取用户喜欢的歌曲
        Set<Long> userLikedSongs = getUserLikedSongs(userId);
        
        if (userLikedSongs.isEmpty()) {
            // 冷启动：返回热门歌单
            return getHotPlaylists(limit);
        }
        
        // 2. 获取所有公开歌单
        List<Playlist> allPlaylists = playlistRepository.findByIsPublicOrderByCreateTimeDesc(true);
        
        // 3. 计算每个歌单与用户兴趣的匹配度
        Map<Long, Double> playlistScores = new HashMap<>();
        
        for (Playlist playlist : allPlaylists) {
            // 排除用户自己创建的歌单
            if (playlist.getUserId().equals(userId)) {
                continue;
            }
            
            // 跳过空歌单
            if (playlist.getSongCount() == null || playlist.getSongCount() == 0) {
                continue;
            }
            
            // 获取歌单中的所有歌曲
            List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistIdOrderByAddTimeDesc(playlist.getId());
            Set<Long> playlistSongIds = playlistSongs.stream()
                .map(PlaylistSong::getSongId)
                .collect(Collectors.toSet());
            
            if (playlistSongIds.isEmpty()) {
                continue;
            }
            
            // 计算交集：歌单中有多少首歌是用户喜欢的
            Set<Long> intersection = new HashSet<>(userLikedSongs);
            intersection.retainAll(playlistSongIds);
            int matchCount = intersection.size();
            
            // 如果没有匹配的歌曲，跳过
            if (matchCount == 0) {
                continue;
            }
            
            // 计算匹配度分数
            // 分数 = 匹配歌曲数 / sqrt(用户喜欢的歌曲数 * 歌单歌曲数) * 热度权重
            double matchRatio = matchCount / Math.sqrt(userLikedSongs.size() * playlistSongIds.size());
            
            // 热度权重：考虑歌单的播放量和收藏量
            double popularityWeight = 1 + Math.log(1 + (playlist.getPlayCount() != null ? playlist.getPlayCount() : 0) 
                                                      + (playlist.getCollectCount() != null ? playlist.getCollectCount() : 0) * 2);
            
            // 最终分数 = 匹配度 * 热度权重
            double score = matchRatio * popularityWeight;
            
            playlistScores.put(playlist.getId(), score);
        }
        
        // 如果没有匹配的歌单，返回热门歌单
        if (playlistScores.isEmpty()) {
            return getHotPlaylists(limit);
        }
        
        // 4. 按分数排序并返回Top N
        List<Long> recommendedPlaylistIds = playlistScores.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(limit)
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
        
        List<Playlist> recommendedPlaylists = playlistRepository.findAllById(recommendedPlaylistIds);
        
        // 触发 creator 的加载
        recommendedPlaylists.forEach(playlist -> {
            if (playlist.getCreator() != null) {
                playlist.getCreator().getNickname();
            }
        });
        
        return recommendedPlaylists;
    }
    
    /**
     * "猜你喜欢"的歌曲推荐（混合推荐策略）
     * 
     * @param userId 用户ID
     * @param limit 推荐数量
     * @return 推荐歌曲列表
     */
    public List<Song> getGuessYouLike(Long userId, int limit) {
        List<Song> recommendations = new ArrayList<>();
        
        // 策略1: 基于协同过滤的推荐（占50%）
        int cfCount = limit / 2;
        List<Song> cfSongs = getPersonalizedSongs(userId, cfCount);
        recommendations.addAll(cfSongs);
        
        // 策略2: 基于用户最近播放的相似歌曲（占30%）
        int similarCount = (int) (limit * 0.3);
        List<Song> similarSongs = getSongsBasedOnRecentPlay(userId, similarCount);
        recommendations.addAll(similarSongs);
        
        // 策略3: 热门新歌（占20%）
        int newCount = limit - recommendations.size();
        List<Song> newSongs = getNewHotSongs(newCount);
        recommendations.addAll(newSongs);
        
        // 去重并限制数量
        return recommendations.stream()
            .distinct()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    // ========== 辅助方法 ==========
    
    /**
     * 获取用户喜欢的歌曲ID集合
     * 包括：收藏的歌曲 + 播放次数>=3的歌曲
     */
    private Set<Long> getUserLikedSongs(Long userId) {
        Set<Long> likedSongs = new HashSet<>();
        
        // 1. 从收藏获取
        List<Favorite> favorites = favoriteRepository.findByUserIdOrderByCreateTimeDesc(userId);
        likedSongs.addAll(favorites.stream()
            .map(Favorite::getSongId)
            .collect(Collectors.toSet()));
        
        // 2. 从播放历史获取（播放次数 >= 3 的歌曲）
        List<PlayHistory> playHistory = playHistoryRepository.findRecentByUserId(
            userId, PageRequest.of(0, 200)
        );
        
        Map<Long, Long> playCount = playHistory.stream()
            .collect(Collectors.groupingBy(
                PlayHistory::getSongId,
                Collectors.counting()
            ));
        
        playCount.entrySet().stream()
            .filter(entry -> entry.getValue() >= 3)
            .forEach(entry -> likedSongs.add(entry.getKey()));
        
        return likedSongs;
    }
    
    /**
     * 找到相似的用户（基于Jaccard相似度）
     */
    private Map<Long, Double> findSimilarUsers(Long currentUserId, Set<Long> currentUserSongs) {
        Map<Long, Double> similarUsers = new HashMap<>();
        
        // 获取所有收藏记录
        List<Favorite> allFavorites = favoriteRepository.findAll();
        
        // 按用户分组统计
        Map<Long, Set<Long>> userSongsMap = new HashMap<>();
        for (Favorite fav : allFavorites) {
            if (fav.getUserId().equals(currentUserId)) continue;
            
            userSongsMap.computeIfAbsent(fav.getUserId(), k -> new HashSet<>())
                .add(fav.getSongId());
        }
        
        // 计算相似度
        for (Map.Entry<Long, Set<Long>> entry : userSongsMap.entrySet()) {
            Long otherUserId = entry.getKey();
            Set<Long> otherUserSongs = entry.getValue();
            
            // 计算Jaccard相似度
            double similarity = calculateJaccardSimilarity(currentUserSongs, otherUserSongs);
            
            // 只保留相似度 > 0.1 的用户
            if (similarity > 0.1) {
                similarUsers.put(otherUserId, similarity);
            }
        }
        
        // 返回Top 10相似用户
        return similarUsers.entrySet().stream()
            .sorted(Map.Entry.<Long, Double>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                Map.Entry::getValue,
                (e1, e2) -> e1,
                LinkedHashMap::new
            ));
    }
    
    /**
     * 计算Jaccard相似系数
     */
    private double calculateJaccardSimilarity(Set<Long> set1, Set<Long> set2) {
        if (set1.isEmpty() || set2.isEmpty()) {
            return 0.0;
        }
        
        Set<Long> intersection = new HashSet<>(set1);
        intersection.retainAll(set2);
        
        Set<Long> union = new HashSet<>(set1);
        union.addAll(set2);
        
        return (double) intersection.size() / union.size();
    }
    
    /**
     * 获取喜欢某首歌的所有用户
     */
    private Set<Long> getUsersWhoLikeSong(Long songId) {
        Set<Long> users = new HashSet<>();
        
        // 从收藏获取
        List<Favorite> favorites = favoriteRepository.findAll();
        users.addAll(favorites.stream()
            .filter(f -> f.getSongId().equals(songId))
            .map(Favorite::getUserId)
            .collect(Collectors.toSet()));
        
        // 从播放历史获取（播放次数 >= 2）
        List<PlayHistory> playHistory = playHistoryRepository.findAll();
        Map<Long, Long> userPlayCount = playHistory.stream()
            .filter(ph -> ph.getSongId().equals(songId))
            .collect(Collectors.groupingBy(
                PlayHistory::getUserId,
                Collectors.counting()
            ));
        
        userPlayCount.entrySet().stream()
            .filter(entry -> entry.getValue() >= 2)
            .forEach(entry -> users.add(entry.getKey()));
        
        return users;
    }
    
    /**
     * 基于用户最近播放的歌曲推荐相似歌曲
     */
    private List<Song> getSongsBasedOnRecentPlay(Long userId, int limit) {
        // 获取用户最近播放的5首歌
        List<PlayHistory> recentPlays = playHistoryRepository.findRecentByUserId(
            userId, PageRequest.of(0, 5)
        );
        
        if (recentPlays.isEmpty()) {
            return Collections.emptyList();
        }
        
        Set<Song> similarSongs = new HashSet<>();
        Set<Long> userLikedSongs = getUserLikedSongs(userId);
        
        // 为每首最近播放的歌曲找相似歌曲
        for (PlayHistory play : recentPlays) {
            List<Song> similar = getSimilarSongs(play.getSongId(), 3);
            for (Song song : similar) {
                // 过滤掉用户已经喜欢的
                if (!userLikedSongs.contains(song.getId())) {
                    similarSongs.add(song);
                }
            }
            if (similarSongs.size() >= limit) break;
        }
        
        return similarSongs.stream()
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取热门歌曲（冷启动使用）
     */
    private List<Song> getHotSongs(int limit) {
        return songRepository.findByStatusOrderByPlayCountDesc(
            1, PageRequest.of(0, limit)
        ).getContent();
    }
    
    /**
     * 获取热门新歌（最近30天的高播放量歌曲）
     */
    private List<Song> getNewHotSongs(int limit) {
        LocalDateTime thirtyDaysAgo = LocalDateTime.now().minusDays(30);
        List<Song> allSongs = songRepository.findAll();
        
        return allSongs.stream()
            .filter(song -> song.getStatus() == 1)
            .filter(song -> song.getCreateTime() != null && 
                           song.getCreateTime().isAfter(thirtyDaysAgo))
            .sorted(Comparator.comparing(Song::getPlayCount).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
    
    /**
     * 获取热门歌单
     */
    private List<Playlist> getHotPlaylists(int limit) {
        List<Playlist> hotPlaylists = playlistRepository.findByIsPublicOrderByCreateTimeDesc(true)
            .stream()
            .sorted(Comparator.comparing(Playlist::getSongCount).reversed())
            .limit(limit)
            .collect(Collectors.toList());
        
        // 触发 creator 的加载
        hotPlaylists.forEach(playlist -> {
            if (playlist.getCreator() != null) {
                playlist.getCreator().getNickname();
            }
        });
        
        return hotPlaylists;
    }
}

