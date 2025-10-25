package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.entity.Playlist;
import com.example.sheepmusic.entity.Song;
import com.example.sheepmusic.service.RecommendationService;
import com.example.sheepmusic.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 推荐系统控制器
 */
@Api(tags = "推荐系统")
@RestController
@RequestMapping("/music/recommend")
@CrossOrigin
public class RecommendationController {
    
    @Autowired
    private RecommendationService recommendationService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    /**
     * 获取个性化推荐歌曲（基于用户协同过滤）
     */
    @ApiOperation("获取个性化推荐歌曲")
    @GetMapping("/personalized")
    public Result<List<Song>> getPersonalizedSongs(
            HttpServletRequest request,
            @ApiParam(value = "推荐数量", defaultValue = "20")
            @RequestParam(defaultValue = "20") int limit
    ) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            List<Song> recommendations = recommendationService.getPersonalizedSongs(userId, limit);
            return Result.success("推荐成功", recommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 猜你喜欢（混合推荐策略）
     */
    @ApiOperation("猜你喜欢")
    @GetMapping("/guess-you-like")
    public Result<List<Song>> getGuessYouLike(
            HttpServletRequest request,
            @ApiParam(value = "推荐数量", defaultValue = "30")
            @RequestParam(defaultValue = "30") int limit
    ) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            List<Song> recommendations = recommendationService.getGuessYouLike(userId, limit);
            return Result.success("推荐成功", recommendations);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取推荐失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取相似歌曲（基于物品协同过滤）
     */
    @ApiOperation("获取相似歌曲")
    @GetMapping("/similar-songs/{songId}")
    public Result<List<Song>> getSimilarSongs(
            @ApiParam(value = "歌曲ID", required = true)
            @PathVariable Long songId,
            @ApiParam(value = "推荐数量", defaultValue = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<Song> similarSongs = recommendationService.getSimilarSongs(songId, limit);
            return Result.success("查询成功", similarSongs);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取相似歌曲失败: " + e.getMessage());
        }
    }
    
    /**
     * 推荐歌单
     */
    @ApiOperation("推荐歌单")
    @GetMapping("/playlists")
    public Result<List<Playlist>> getRecommendedPlaylists(
            HttpServletRequest request,
            @ApiParam(value = "推荐数量", defaultValue = "10")
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            Long userId = getUserIdFromToken(request);
            if (userId == null) {
                return Result.error("请先登录");
            }
            
            List<Playlist> playlists = recommendationService.getRecommendedPlaylists(userId, limit);
            return Result.success("推荐成功", playlists);
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("获取推荐歌单失败: " + e.getMessage());
        }
    }
    
    /**
     * 从请求头中获取用户ID
     */
    private Long getUserIdFromToken(HttpServletRequest request) {
        try {
            String token = request.getHeader("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);
                return jwtUtil.getUserIdFromToken(token);
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }
}

