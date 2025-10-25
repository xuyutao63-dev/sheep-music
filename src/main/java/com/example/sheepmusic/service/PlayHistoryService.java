package com.example.sheepmusic.service;

import com.example.sheepmusic.dto.PlayHistoryDTO;
import com.example.sheepmusic.entity.PlayHistory;
import com.example.sheepmusic.repository.PlayHistoryRepository;
import com.example.sheepmusic.repository.SongRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 播放历史业务逻辑层
 */
@Service
public class PlayHistoryService {

    @Autowired
    private PlayHistoryRepository playHistoryRepository;

    @Autowired
    private SongRepository songRepository;

    /**
     * 添加播放历史记录
     * 每次播放歌曲时调用
     */
    @Transactional
    public void addPlayHistory(Long userId, Long songId, Integer playDuration) {
        // 验证歌曲是否存在
        songRepository.findById(songId)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        // 创建播放历史记录
        PlayHistory playHistory = new PlayHistory();
        playHistory.setUserId(userId);
        playHistory.setSongId(songId);
        playHistory.setPlayDuration(playDuration);
        
        playHistoryRepository.save(playHistory);
    }

    /**
     * 添加播放历史记录（不记录播放时长）
     */
    @Transactional
    public void addPlayHistory(Long userId, Long songId) {
        addPlayHistory(userId, songId, null);
    }

    /**
     * 获取用户的播放历史列表（分页）
     * @param distinct 是否去重（每首歌只显示最新的播放记录）
     */
    public Page<PlayHistory> getPlayHistoryList(Long userId, Pageable pageable, boolean distinct) {
        if (distinct) {
            // 去重模式：每首歌只显示最新的播放记录
            return playHistoryRepository.findLatestByUserIdGroupBySong(userId, pageable);
        } else {
            // 完整模式：显示所有播放记录
            return playHistoryRepository.findByUserIdWithSongDetails(userId, pageable);
        }
    }
    
    /**
     * 获取用户的播放历史列表（分页，默认去重）
     */
    public Page<PlayHistory> getPlayHistoryList(Long userId, Pageable pageable) {
        return getPlayHistoryList(userId, pageable, true);
    }
    
    /**
     * 获取用户的播放历史列表（包含播放次数）
     * 默认去重，每首歌只显示最新的播放记录，但会附带该歌曲的总播放次数
     */
    @Transactional(readOnly = true)
    public Page<PlayHistoryDTO> getPlayHistoryWithCount(Long userId, Pageable pageable) {
        // 获取所有播放历史（不分页，因为需要先去重）
        List<PlayHistory> allHistory = playHistoryRepository.findByUserIdOrderByPlayTimeDesc(userId, org.springframework.data.domain.PageRequest.of(0, Integer.MAX_VALUE)).getContent();
        
        // 手动去重：使用 Map 保存每首歌的最新播放记录
        java.util.Map<Long, PlayHistory> latestByeSongId = new java.util.LinkedHashMap<>();
        for (PlayHistory history : allHistory) {
            if (!latestByeSongId.containsKey(history.getSongId())) {
                latestByeSongId.put(history.getSongId(), history);
            }
        }
        
        // 转换为 List
        List<PlayHistory> distinctHistory = new java.util.ArrayList<>(latestByeSongId.values());
        
        // 手动分页
        int start = (int) pageable.getOffset();
        int end = Math.min(start + pageable.getPageSize(), distinctHistory.size());
        List<PlayHistory> pagedHistory = start >= distinctHistory.size() ? 
            new java.util.ArrayList<>() : 
            distinctHistory.subList(start, end);
        
        // 转换为 DTO，添加播放次数信息
        List<PlayHistoryDTO> dtoList = pagedHistory.stream()
            .map(history -> {
                Long playCount = playHistoryRepository.countByUserIdAndSongId(userId, history.getSongId());
                return PlayHistoryDTO.fromEntity(history, playCount);
            })
            .collect(Collectors.toList());
        
        return new PageImpl<>(dtoList, pageable, distinctHistory.size());
    }

    /**
     * 获取用户最近播放的N首歌曲
     */
    public List<PlayHistory> getRecentPlayHistory(Long userId, int limit) {
        Pageable pageable = PageRequest.of(0, limit);
        return playHistoryRepository.findRecentByUserId(userId, pageable);
    }

    /**
     * 统计用户的播放历史总数
     * @param distinct 是否统计不同歌曲的数量（而非总播放次数）
     */
    public long countPlayHistory(Long userId, boolean distinct) {
        if (distinct) {
            return playHistoryRepository.countDistinctSongsByUserId(userId);
        } else {
            return playHistoryRepository.countByUserId(userId);
        }
    }
    
    /**
     * 统计用户的播放历史总数（默认去重统计）
     */
    public long countPlayHistory(Long userId) {
        return countPlayHistory(userId, true);
    }

    /**
     * 清空用户的播放历史
     */
    @Transactional
    public void clearPlayHistory(Long userId) {
        playHistoryRepository.deleteByUserId(userId);
    }

    /**
     * 删除单条播放历史记录
     */
    @Transactional
    public void deletePlayHistory(Long historyId, Long userId) {
        PlayHistory playHistory = playHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("播放历史记录不存在"));
        
        // 验证是否是当前用户的历史记录
        if (!playHistory.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此记录");
        }
        
        playHistoryRepository.delete(playHistory);
    }

    /**
     * 清理旧的播放历史（例如：保留最近3个月的）
     * 可以定时任务调用
     */
    @Transactional
    public void cleanupOldHistory(int monthsToKeep) {
        LocalDateTime cutoffDate = LocalDateTime.now().minusMonths(monthsToKeep);
        playHistoryRepository.deleteByPlayTimeBefore(cutoffDate);
    }

    /**
     * 获取歌曲的总播放次数
     */
    public long getSongPlayCount(Long songId) {
        return playHistoryRepository.countBySongId(songId);
    }

    /**
     * 查询用户在指定时间范围内的播放历史
     */
    public List<PlayHistory> getPlayHistoryByTimeRange(Long userId, LocalDateTime startTime, LocalDateTime endTime) {
        return playHistoryRepository.findByUserIdAndTimeRange(userId, startTime, endTime);
    }
}

