package com.example.sheepmusic.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 歌手请求DTO
 */
@Data
public class ArtistRequest {
    
    /**
     * 歌手名称
     */
    @NotBlank(message = "歌手名称不能为空")
    private String name;
    
    /**
     * 歌手头像URL
     */
    private String avatar;
    
    /**
     * 歌手简介
     */
    private String description;
    
    /**
     * 国家/地区
     */
    private String region;
}

