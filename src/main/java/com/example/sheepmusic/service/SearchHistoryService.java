package com.example.sheepmusic.service;

import com.example.sheepmusic.entity.SearchHistory;
import com.example.sheepmusic.repository.SearchHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 搜索历史服务
 */
@Service
public class SearchHistoryService {
    
    @Autowired
    private SearchHistoryRepository searchHistoryRepository;
    
    /**
     * 最大历史记录数
     */
    private static final int MAX_HISTORY_SIZE = 10;
    
    /**
     * 获取用户的搜索历史（最近10条）
     */
    public List<SearchHistory> getUserSearchHistory(Long userId) {
        List<SearchHistory> allHistory = searchHistoryRepository.findByUserIdOrderBySearchTimeDesc(userId);
        
        // 只返回最近10条
        return allHistory.stream()
                .limit(MAX_HISTORY_SIZE)
                .collect(Collectors.toList());
    }
    
    /**
     * 添加搜索记录
     * 如果关键词已存在，更新时间；否则新增
     */
    @Transactional
    public SearchHistory addSearchHistory(Long userId, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        
        // 检查是否已存在相同关键词
        SearchHistory existing = searchHistoryRepository.findByUserIdAndKeyword(userId, keyword);
        
        if (existing != null) {
            // 如果存在，删除旧记录，重新创建（这样时间会更新到最新）
            searchHistoryRepository.delete(existing);
        }
        
        // 创建新记录
        SearchHistory newHistory = new SearchHistory();
        newHistory.setUserId(userId);
        newHistory.setKeyword(keyword);
        SearchHistory saved = searchHistoryRepository.save(newHistory);
        
        // 检查总数，如果超过10条，删除最旧的
        cleanupOldHistory(userId);
        
        return saved;
    }
    
    /**
     * 删除单条搜索历史
     */
    @Transactional
    public void deleteSearchHistory(Long userId, Long historyId) {
        SearchHistory history = searchHistoryRepository.findById(historyId)
                .orElseThrow(() -> new RuntimeException("搜索历史不存在"));
        
        // 验证是否属于当前用户
        if (!history.getUserId().equals(userId)) {
            throw new RuntimeException("无权删除此搜索历史");
        }
        
        searchHistoryRepository.deleteById(historyId);
    }
    
    /**
     * 清空用户的所有搜索历史
     */
    @Transactional
    public void clearUserSearchHistory(Long userId) {
        searchHistoryRepository.deleteByUserId(userId);
    }
    
    /**
     * 清理旧历史记录，只保留最近10条
     */
    private void cleanupOldHistory(Long userId) {
        List<SearchHistory> allHistory = searchHistoryRepository.findByUserIdOrderBySearchTimeDesc(userId);
        
        if (allHistory.size() > MAX_HISTORY_SIZE) {
            // 删除超出部分
            List<SearchHistory> toDelete = allHistory.subList(MAX_HISTORY_SIZE, allHistory.size());
            searchHistoryRepository.deleteAll(toDelete);
        }
    }
    
    /**
     * 获取热门搜索关键词
     * @param limit 返回的数量限制
     * @return 热门关键词列表
     */
    public List<String> getHotSearchKeywords(int limit) {
        List<Object[]> results = searchHistoryRepository.findHotKeywords();
        
        return results.stream()
                .limit(limit)
                .map(result -> (String) result[0])  // 只返回关键词
                .collect(Collectors.toList());
    }
}

