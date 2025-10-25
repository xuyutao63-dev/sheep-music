package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.entity.Playlist;
import com.example.sheepmusic.entity.PlaylistSong;
import com.example.sheepmusic.entity.User;
import com.example.sheepmusic.service.PlaylistService;
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
 * 歌单控制器
 */
@Api(tags = "歌单管理")
@RestController
@RequestMapping("/api/playlist")
@CrossOrigin
public class PlaylistController {
    
    @Autowired
    private PlaylistService playlistService;
    
    // ==================== 我的歌单管理 ====================
    
    /**
     * 创建歌单
     */
    @ApiOperation("创建歌单")
    @PostMapping("/create")
    public Result<Playlist> createPlaylist(
            @AuthenticationPrincipal User user,
            @RequestBody Playlist playlist
    ) {
        try {
            playlist.setUserId(user.getId());
            Playlist created = playlistService.createPlaylist(playlist);
            return Result.success("创建成功", created);
        } catch (Exception e) {
            return Result.error("创建失败: " + e.getMessage());
        }
    }
    
    /**
     * 更新歌单信息
     */
    @ApiOperation("更新歌单信息")
    @PutMapping("/{id}")
    public Result<Playlist> updatePlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestBody Playlist playlist
    ) {
        try {
            Playlist updated = playlistService.updatePlaylist(id, playlist, user.getId());
            return Result.success("更新成功", updated);
        } catch (Exception e) {
            return Result.error("更新失败: " + e.getMessage());
        }
    }
    
    /**
     * 删除歌单
     */
    @ApiOperation("删除歌单")
    @DeleteMapping("/{id}")
    public Result<?> deletePlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        try {
            playlistService.deletePlaylist(id, user.getId());
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我的歌单列表
     */
    @ApiOperation("获取我的歌单列表")
    @GetMapping("/my")
    public Result<Page<Playlist>> getMyPlaylists(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Playlist> playlists = playlistService.getUserPlaylists(user.getId(), pageable);
            return Result.success("查询成功", playlists);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取我的所有歌单（不分页，用于选择器）
     */
    @ApiOperation("获取我的所有歌单")
    @GetMapping("/my/all")
    public Result<List<Playlist>> getMyAllPlaylists(
            @AuthenticationPrincipal User user
    ) {
        try {
            List<Playlist> playlists = playlistService.getUserAllPlaylists(user.getId());
            return Result.success("查询成功", playlists);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 统计我的歌单数量
     */
    @ApiOperation("统计我的歌单数量")
    @GetMapping("/my/count")
    public Result<Map<String, Long>> countMyPlaylists(
            @AuthenticationPrincipal User user
    ) {
        try {
            long count = playlistService.countUserPlaylists(user.getId());
            Map<String, Long> data = new HashMap<>();
            data.put("count", count);
            return Result.success("查询成功", data);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    // ==================== 歌单内容管理 ====================
    
    /**
     * 添加歌曲到歌单
     */
    @ApiOperation("添加歌曲到歌单")
    @PostMapping("/{playlistId}/songs/{songId}")
    public Result<PlaylistSong> addSongToPlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable Long playlistId,
            @PathVariable Long songId
    ) {
        try {
            PlaylistSong playlistSong = playlistService.addSongToPlaylist(playlistId, songId, user.getId());
            return Result.success("添加成功", playlistSong);
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 批量添加歌曲到歌单
     */
    @ApiOperation("批量添加歌曲到歌单")
    @PostMapping("/{playlistId}/songs/batch")
    public Result<?> addSongsToPlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable Long playlistId,
            @RequestBody List<Long> songIds
    ) {
        try {
            playlistService.addSongsToPlaylist(playlistId, songIds, user.getId());
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }
    
    /**
     * 从歌单移除歌曲
     */
    @ApiOperation("从歌单移除歌曲")
    @DeleteMapping("/{playlistId}/songs/{songId}")
    public Result<?> removeSongFromPlaylist(
            @AuthenticationPrincipal User user,
            @PathVariable Long playlistId,
            @PathVariable Long songId
    ) {
        try {
            playlistService.removeSongFromPlaylist(playlistId, songId, user.getId());
            return Result.success("移除成功");
        } catch (Exception e) {
            return Result.error("移除失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取歌单的歌曲列表
     */
    @ApiOperation("获取歌单的歌曲列表")
    @GetMapping("/{playlistId}/songs")
    public Result<Page<PlaylistSong>> getPlaylistSongs(
            @PathVariable Long playlistId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "50") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PlaylistSong> songs = playlistService.getPlaylistSongs(playlistId, pageable);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    // ==================== 歌单详情 ====================
    
    /**
     * 获取歌单详情
     */
    @ApiOperation("获取歌单详情")
    @GetMapping("/{id}")
    public Result<Playlist> getPlaylistDetail(
            @PathVariable Long id
    ) {
        try {
            Playlist playlist = playlistService.getPlaylistById(id);
            return Result.success("查询成功", playlist);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 增加播放次数
     */
    @ApiOperation("增加播放次数")
    @PostMapping("/{id}/play")
    public Result<?> incrementPlayCount(
            @PathVariable Long id
    ) {
        try {
            playlistService.incrementPlayCount(id);
            return Result.success("记录成功");
        } catch (Exception e) {
            return Result.error("记录失败: " + e.getMessage());
        }
    }
    
    /**
     * 设置歌单公开/私有
     */
    @ApiOperation("设置歌单公开/私有")
    @PostMapping("/{id}/visibility")
    public Result<?> setPlaylistVisibility(
            @AuthenticationPrincipal User user,
            @PathVariable Long id,
            @RequestParam boolean isPublic
    ) {
        try {
            playlistService.setPlaylistPublic(id, isPublic, user.getId());
            return Result.success(isPublic ? "已设为公开" : "已设为私有");
        } catch (Exception e) {
            return Result.error("设置失败: " + e.getMessage());
        }
    }
    
    // ==================== 歌单广场 ====================
    
    /**
     * 获取公开歌单列表
     */
    @ApiOperation("获取公开歌单列表")
    @GetMapping("/square")
    public Result<Page<Playlist>> getPublicPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(required = false) String category
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Playlist> playlists;
            
            if (category != null && !category.isEmpty()) {
                playlists = playlistService.getPublicPlaylistsByCategory(category, pageable);
            } else {
                playlists = playlistService.getPublicPlaylists(pageable);
            }
            
            return Result.success("查询成功", playlists);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 获取热门公开歌单
     */
    @ApiOperation("获取热门公开歌单")
    @GetMapping("/square/hot")
    public Result<Page<Playlist>> getHotPlaylists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Playlist> playlists = playlistService.getHotPlaylists(pageable);
            return Result.success("查询成功", playlists);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
    
    /**
     * 搜索公开歌单
     */
    @ApiOperation("搜索公开歌单")
    @GetMapping("/square/search")
    public Result<Page<Playlist>> searchPublicPlaylists(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Playlist> playlists = playlistService.searchPublicPlaylists(keyword, pageable);
            return Result.success("查询成功", playlists);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}

