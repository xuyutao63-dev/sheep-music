package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.SearchHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 搜索历史Repository
 */
@Repository
public interface SearchHistoryRepository extends JpaRepository<SearchHistory, Long> {
    
    /**
     * 根据用户ID查询搜索历史，按时间倒序
     */
    List<SearchHistory> findByUserIdOrderBySearchTimeDesc(Long userId);
    
    /**
     * 根据用户ID和关键词查询
     */
    SearchHistory findByUserIdAndKeyword(Long userId, String keyword);
    
    /**
     * 根据用户ID删除所有搜索历史
     */
    @Modifying
    @Query("DELETE FROM SearchHistory sh WHERE sh.userId = ?1")
    void deleteByUserId(Long userId);
    
    /**
     * 统计用户的搜索历史数量
     */
    long countByUserId(Long userId);
    
    /**
     * 获取热门搜索关键词（按搜索次数统计，取前N条）
     */
    @Query(value = "SELECT keyword, COUNT(*) as count " +
                   "FROM search_history " +
                   "GROUP BY keyword " +
                   "ORDER BY count DESC, MAX(search_time) DESC",
           nativeQuery = true)
    List<Object[]> findHotKeywords();
}

