package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.dto.SongRequest;
import com.example.sheepmusic.entity.Song;
import com.example.sheepmusic.service.SongService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * 歌曲管理控制器（管理员）
 */
@Api(tags = "歌曲管理")
@RestController
@RequestMapping("/admin/song")
@CrossOrigin
public class SongController {
    
    @Autowired
    private SongService songService;
    
    /**
     * 创建歌曲
     */
    @ApiOperation("创建歌曲")
    @PostMapping
    public Result<Song> createSong(@Valid @RequestBody SongRequest request) {
        try {
            Song song = songService.createSong(request);
            return Result.success("创建成功", song);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新歌曲
     */
    @ApiOperation("更新歌曲")
    @PutMapping("/{id}")
    public Result<Song> updateSong(@PathVariable Long id, @Valid @RequestBody SongRequest request) {
        try {
            Song song = songService.updateSong(id, request);
            return Result.success("更新成功", song);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除歌曲
     */
    @ApiOperation("删除歌曲")
    @DeleteMapping("/{id}")
    public Result<Void> deleteSong(@PathVariable Long id) {
        try {
            songService.deleteSong(id);
            Result<Void> result = Result.success();
            result.setMessage("删除成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取歌曲详情
     */
    @ApiOperation("获取歌曲详情")
    @GetMapping("/{id}")
    public Result<Song> getSong(@PathVariable Long id) {
        try {
            Song song = songService.getSong(id);
            return Result.success("查询成功", song);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取歌曲列表（分页）
     */
    @ApiOperation("获取歌曲列表")
    @GetMapping("/list")
    public Result<Page<Song>> getSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createTime") String sortBy,
            @RequestParam(defaultValue = "desc") String order
    ) {
        try {
            Sort sort = order.equalsIgnoreCase("asc") 
                    ? Sort.by(sortBy).ascending() 
                    : Sort.by(sortBy).descending();
            Pageable pageable = PageRequest.of(page, size, sort);
            Page<Song> songs = songService.getSongs(pageable);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据歌手获取歌曲
     */
    @ApiOperation("根据歌手获取歌曲")
    @GetMapping("/artist/{artistId}")
    public Result<List<Song>> getSongsByArtist(@PathVariable Long artistId) {
        try {
            List<Song> songs = songService.getSongsByArtist(artistId);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

