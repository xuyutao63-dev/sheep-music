package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.dto.PlayHistoryDTO;
import com.example.sheepmusic.entity.PlayHistory;
import com.example.sheepmusic.entity.User;
import com.example.sheepmusic.service.PlayHistoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 播放历史控制器
 */
@Api(tags = "播放历史管理")
@RestController
@RequestMapping("/api/user/play-history")
public class PlayHistoryController {

    @Autowired
    private PlayHistoryService playHistoryService;

    /**
     * 添加播放历史记录
     * POST /api/user/play-history
     * Body: { "songId": 123, "playDuration": 180 }
     */
    @ApiOperation("添加播放历史")
    @PostMapping
    public Result<?> addPlayHistory(
            @AuthenticationPrincipal User user,
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long songId = Long.valueOf(request.get("songId").toString());
            Integer playDuration = request.get("playDuration") != null 
                ? Integer.valueOf(request.get("playDuration").toString()) 
                : null;
            
            playHistoryService.addPlayHistory(user.getId(), songId, playDuration);
            return Result.success("添加成功");
        } catch (Exception e) {
            return Result.error("添加失败: " + e.getMessage());
        }
    }

    /**
     * 获取播放历史列表（分页，去重，包含播放次数）
     * GET /api/user/play-history/list?page=0&size=20
     * 注意：返回去重后的播放历史，每首歌只显示最新的播放记录，但会附带该歌曲的总播放次数
     */
    @ApiOperation("获取播放历史列表")
    @GetMapping("/list")
    public Result<Page<PlayHistoryDTO>> getPlayHistoryList(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<PlayHistoryDTO> history = playHistoryService.getPlayHistoryWithCount(user.getId(), pageable);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }

    /**
     * 统计播放历史数量
     * GET /api/user/play-history/count
     */
    @ApiOperation("统计播放历史数量")
    @GetMapping("/count")
    public Result<Map<String, Long>> countPlayHistory(
            @AuthenticationPrincipal User user
    ) {
        try {
            long count = playHistoryService.countPlayHistory(user.getId());
            Map<String, Long> result = new HashMap<>();
            result.put("count", count);
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("统计失败: " + e.getMessage());
        }
    }

    /**
     * 清空播放历史
     * DELETE /api/user/play-history/clear
     */
    @ApiOperation("清空播放历史")
    @DeleteMapping("/clear")
    public Result<?> clearPlayHistory(
            @AuthenticationPrincipal User user
    ) {
        try {
            playHistoryService.clearPlayHistory(user.getId());
            return Result.success("清空成功");
        } catch (Exception e) {
            return Result.error("清空失败: " + e.getMessage());
        }
    }

    /**
     * 删除单条播放历史
     * DELETE /api/user/play-history/{id}
     */
    @ApiOperation("删除单条播放历史")
    @DeleteMapping("/{id}")
    public Result<?> deletePlayHistory(
            @AuthenticationPrincipal User user,
            @PathVariable Long id
    ) {
        try {
            playHistoryService.deletePlayHistory(id, user.getId());
            return Result.success("删除成功");
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }

    /**
     * 获取最近播放的歌曲
     * GET /api/user/play-history/recent?limit=10
     */
    @ApiOperation("获取最近播放的歌曲")
    @GetMapping("/recent")
    public Result<?> getRecentPlayHistory(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "10") int limit
    ) {
        try {
            List<PlayHistory> history = playHistoryService.getRecentPlayHistory(user.getId(), limit);
            return Result.success(history);
        } catch (Exception e) {
            return Result.error("查询失败: " + e.getMessage());
        }
    }
}

