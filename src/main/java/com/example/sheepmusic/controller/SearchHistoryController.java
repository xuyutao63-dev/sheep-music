package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.dto.SearchHistoryRequest;
import com.example.sheepmusic.entity.SearchHistory;
import com.example.sheepmusic.entity.User;
import com.example.sheepmusic.service.SearchHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 搜索历史控制器
 */
@Api(tags = "搜索历史管理")
@RestController
@RequestMapping("/api/user/search-history")
@CrossOrigin
public class SearchHistoryController {
    
    @Autowired
    private SearchHistoryService searchHistoryService;
    
    /**
     * 获取热门搜索关键词（公开接口，无需登录）
     */
    @ApiOperation("获取热门搜索")
    @GetMapping("/hot")
    public Result<List<String>> getHotSearchKeywords(
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<String> hotKeywords = searchHistoryService.getHotSearchKeywords(limit);
            return Result.success("查询成功", hotKeywords);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取当前用户的搜索历史
     */
    @ApiOperation("获取搜索历史")
    @GetMapping
    public Result<List<SearchHistory>> getSearchHistory(@AuthenticationPrincipal User user) {
        try {
            List<SearchHistory> historyList = searchHistoryService.getUserSearchHistory(user.getId());
            return Result.success("查询成功", historyList);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 添加搜索记录
     */
    @ApiOperation("添加搜索记录")
    @PostMapping
    public Result<Void> addSearchHistory(
            @AuthenticationPrincipal User user,
            @RequestBody SearchHistoryRequest request
    ) {
        try {
            String keyword = request.getKeyword();
            if (keyword == null || keyword.trim().isEmpty()) {
                return Result.error("搜索关键词不能为空");
            }
            
            searchHistoryService.addSearchHistory(user.getId(), keyword.trim());
            Result<Void> result = Result.success();
            result.setMessage("添加成功");
            return result;
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除单条搜索历史
     */
    @ApiOperation("删除搜索历史")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSearchHistory(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        try {
            searchHistoryService.deleteSearchHistory(user.getId(), id);
            Result<Void> result = Result.success();
            result.setMessage("删除成功");
            return result;
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 清空所有搜索历史
     */
    @ApiOperation("清空搜索历史")
    @DeleteMapping("/clear")
    public Result<Void> clearSearchHistory(@AuthenticationPrincipal User user) {
        try {
            searchHistoryService.clearUserSearchHistory(user.getId());
            Result<Void> result = Result.success();
            result.setMessage("清空成功");
            return result;
        } catch (Exception e) {
            return Result.error("清空失败: " + e.getMessage());
        }
    }
}

