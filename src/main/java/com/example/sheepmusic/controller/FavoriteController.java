package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.entity.Favorite;
import com.example.sheepmusic.entity.User;
import com.example.sheepmusic.service.FavoriteService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 收藏控制器
 */
@Api(tags = "收藏管理")
@RestController
@RequestMapping("/api/user/favorite")
@CrossOrigin
public class FavoriteController {
    
    @Autowired
    private FavoriteService favoriteService;
    
    /**
     * 添加收藏
     */
    @ApiOperation("添加收藏")
    @PostMapping("/{songId}")
    public Result<?> addFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long songId
    ) {
        try {
            favoriteService.addFavorite(user.getId(), songId);
            return Result.success("收藏成功");
        } catch (Exception e) {
            return Result.error("收藏失败: " + e.getMessage());
        }
    }
    
    /**
     * 取消收藏
     */
    @ApiOperation("取消收藏")
    @DeleteMapping("/{songId}")
    public Result<?> removeFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long songId
    ) {
        try {
            favoriteService.removeFavorite(user.getId(), songId);
            return Result.success("取消收藏成功");
        } catch (Exception e) {
            return Result.error("取消收藏失败: " + e.getMessage());
        }
    }
    
    /**
     * 切换收藏状态
     */
    @ApiOperation("切换收藏状态")
    @PostMapping("/toggle/{songId}")
    public Result<Map<String, Boolean>> toggleFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long songId
    ) {
        try {
            boolean isFavorite = favoriteService.toggleFavorite(user.getId(), songId);
            
            Map<String, Boolean> data = new HashMap<>();
            data.put("isFavorite", isFavorite);
            
            return Result.success(isFavorite ? "收藏成功" : "取消收藏成功", data);
        } catch (Exception e) {
            return Result.error("操作失败: " + e.getMessage());
        }
    }
    
    /**
     * 检查是否已收藏
     */
    @ApiOperation("检查是否已收藏")
    @GetMapping("/check/{songId}")
    public Result<Map<String, Boolean>> checkFavorite(
            @AuthenticationPrincipal User user,
            @PathVariable Long songId
    ) {
        try {
            boolean isFavorite = favoriteService.isFavorite(user.getId(), songId);
            
            Map<String, Boolean> data = new HashMap<>();
            data.put("isFavorite", isFavorite);
            
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量检查收藏状态
     */
    @ApiOperation("批量检查收藏状态")
    @PostMapping("/batch-check")
    public Result<Map<Long, Boolean>> batchCheckFavorites(
            @AuthenticationPrincipal User user,
            @RequestBody List<Long> songIds
    ) {
        try {
            Map<Long, Boolean> favorites = favoriteService.batchCheckFavorites(user.getId(), songIds);
            return Result.success("查询成功", favorites);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我的收藏列表
     */
    @ApiOperation("获取我的收藏列表")
    @GetMapping("/list")
    public Result<Page<Favorite>> getMyFavorites(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Favorite> favorites = favoriteService.getUserFavorites(user.getId(), pageable);
            return Result.success("查询成功", favorites);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计我的收藏数量
     */
    @ApiOperation("统计我的收藏数量")
    @GetMapping("/count")
    public Result<Map<String, Long>> countMyFavorites(
            @AuthenticationPrincipal User user
    ) {
        try {
            long count = favoriteService.countUserFavorites(user.getId());
            
            Map<String, Long> data = new HashMap<>();
            data.put("count", count);
            
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}

