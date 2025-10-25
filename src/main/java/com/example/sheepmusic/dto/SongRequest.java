package com.example.sheepmusic.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * 歌曲请求DTO
 */
@Data
public class SongRequest {
    
    /**
     * 歌曲名称
     */
    @NotBlank(message = "歌曲名称不能为空")
    private String title;
    
    /**
     * 歌手ID列表（支持多歌手）
     */
    @NotEmpty(message = "至少需要选择一位歌手")
    private List<Long> artistIds;
    
    /**
     * 专辑ID（可选）
     */
    private Long albumId;
    
    /**
     * 专辑名称（可选）
     */
    private String albumName;
    
    /**
     * 歌曲时长（秒）
     */
    private Integer duration;
    
    /**
     * 封面URL
     */
    private String cover;
    
    /**
     * 音频文件URL
     */
    @NotBlank(message = "音频文件URL不能为空")
    private String url;
    
    /**
     * 歌词（可选）
     */
    private String lyric;
    
    /**
     * 歌曲状态：0-下架，1-上架
     */
    private Integer status;
}

