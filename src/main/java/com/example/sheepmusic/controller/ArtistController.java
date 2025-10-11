package com.example.sheepmusic.controller;

import com.example.sheepmusic.common.Result;
import com.example.sheepmusic.dto.ArtistRequest;
import com.example.sheepmusic.entity.Artist;
import com.example.sheepmusic.service.ArtistService;
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
 * 歌手管理控制器（管理员）
 */
@Api(tags = "歌手管理")
@RestController
@RequestMapping("/admin/artist")
@CrossOrigin
public class ArtistController {
    
    @Autowired
    private ArtistService artistService;
    
    /**
     * 创建歌手
     */
    @ApiOperation("创建歌手")
    @PostMapping
    public Result<Artist> createArtist(@Valid @RequestBody ArtistRequest request) {
        try {
            Artist artist = artistService.createArtist(request);
            return Result.success("创建成功", artist);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 更新歌手
     */
    @ApiOperation("更新歌手")
    @PutMapping("/{id}")
    public Result<Artist> updateArtist(@PathVariable Long id, @Valid @RequestBody ArtistRequest request) {
        try {
            Artist artist = artistService.updateArtist(id, request);
            return Result.success("更新成功", artist);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 删除歌手
     */
    @ApiOperation("删除歌手")
    @DeleteMapping("/{id}")
    public Result<Void> deleteArtist(@PathVariable Long id) {
        try {
            artistService.deleteArtist(id);
            Result<Void> result = Result.success();
            result.setMessage("删除成功");
            return result;
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取歌手详情
     */
    @ApiOperation("获取歌手详情")
    @GetMapping("/{id}")
    public Result<Artist> getArtist(@PathVariable Long id) {
        try {
            Artist artist = artistService.getArtist(id);
            return Result.success("查询成功", artist);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取歌手列表（分页）
     */
    @ApiOperation("获取歌手列表")
    @GetMapping("/list")
    public Result<Page<Artist>> getArtists(
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
            Page<Artist> artists = artistService.getArtists(pageable);
            return Result.success("查询成功", artists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
    
    /**
     * 获取所有歌手（不分页，用于下拉选择）
     */
    @ApiOperation("获取所有歌手")
    @GetMapping("/all")
    public Result<List<Artist>> getAllArtists() {
        try {
            List<Artist> artists = artistService.getAllArtists();
            return Result.success("查询成功", artists);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}

