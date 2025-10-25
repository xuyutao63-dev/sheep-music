package com.example.sheepmusic.dto;

import lombok.Data;

/**
 * 搜索历史请求DTO
 */
@Data
public class SearchHistoryRequest {
    /**
     * 搜索关键词
     */
    private String keyword;
}

