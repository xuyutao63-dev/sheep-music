package com.example.sheepmusic.dto;

import com.example.sheepmusic.entity.PlayHistory;
import com.example.sheepmusic.entity.Song;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 播放历史 DTO
 * 包含额外的播放次数信息
 */
@Data
public class PlayHistoryDTO {
    
    /**
     * 播放历史记录 ID
     */
    private Long id;
    
    /**
     * 歌曲信息
     */
    private Song song;
    
    /**
     * 最后播放时间
     */
    private LocalDateTime playTime;
    
    /**
     * 播放次数（用户播放该歌曲的总次数）
     */
    private Long playCount;
    
    /**
     * 从 PlayHistory 实体转换
     */
    public static PlayHistoryDTO fromEntity(PlayHistory playHistory, Long playCount) {
        PlayHistoryDTO dto = new PlayHistoryDTO();
        dto.setId(playHistory.getId());
        dto.setSong(playHistory.getSong());
        dto.setPlayTime(playHistory.getPlayTime());
        dto.setPlayCount(playCount);
        return dto;
    }
}

