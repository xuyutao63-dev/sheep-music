package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.entity.Artist;
import com.example.sheepmusic.entity.Song;
import com.example.sheepmusic.service.ArtistService;
import com.example.sheepmusic.service.SongService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 音乐浏览控制器（用户端）
 */
@Api(tags = "音乐浏览")
@RestController
@RequestMapping("/music")
@CrossOrigin
public class MusicController {
    
    @Autowired
    private SongService songService;
    
    @Autowired
    private ArtistService artistService;
    
    /**
     * 获取歌曲详情
     */
    @ApiOperation("获取歌曲详情")
    @GetMapping("/song/{id}")
    public Result<Song> getSong(@PathVariable Long id) {
        try {
            Song song = songService.getSong(id);
            return Result.success("查询成功", song);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 搜索歌曲
     */
    @ApiOperation("搜索歌曲")
    @GetMapping("/search")
    public Result<Page<Song>> searchSongs(
            @RequestParam String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Song> songs = songService.searchSongs(keyword, pageable);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取热门歌曲
     */
    @ApiOperation("获取热门歌曲")
    @GetMapping("/hot")
    public Result<Page<Song>> getHotSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Song> songs = songService.getHotSongs(pageable);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取最新歌曲
     */
    @ApiOperation("获取最新歌曲")
    @GetMapping("/new")
    public Result<Page<Song>> getNewSongs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<Song> songs = songService.getNewSongs(pageable);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 根据歌手获取歌曲
     */
    @ApiOperation("根据歌手获取歌曲")
    @GetMapping("/artist/{artistId}/songs")
    public Result<List<Song>> getSongsByArtist(@PathVariable Long artistId) {
        try {
            List<Song> songs = songService.getSongsByArtist(artistId);
            return Result.success("查询成功", songs);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 播放歌曲（增加播放次数）
     */
    @ApiOperation("播放歌曲")
    @PostMapping("/play/{id}")
    public Result<Void> playSong(@PathVariable Long id) {
        try {
            songService.incrementPlayCount(id);
            Result<Void> result = Result.success();
            result.setMessage("播放成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取歌手列表
     */
    @ApiOperation("获取歌手列表")
    @GetMapping("/artists")
    public Result<Page<Artist>> getArtists(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size
    ) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by("createTime").descending());
            Page<Artist> artists = artistService.getArtists(pageable);
            return Result.success("查询成功", artists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取歌手详情
     */
    @ApiOperation("获取歌手详情")
    @GetMapping("/artist/{id}")
    public Result<Artist> getArtist(@PathVariable Long id) {
        try {
            Artist artist = artistService.getArtist(id);
            return Result.success("查询成功", artist);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

