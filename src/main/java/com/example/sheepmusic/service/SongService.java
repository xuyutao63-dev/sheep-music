package com.example.sheepmusic.service;

import com.example.sheepmusic.dto.SongRequest;
import com.example.sheepmusic.entity.Song;
import com.example.sheepmusic.repository.SongRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 歌曲服务类
 */
@Service
public class SongService {
    
    @Autowired
    private SongRepository songRepository;
    
    /**
     * 创建歌曲
     */
    public Song createSong(SongRequest request) {
        Song song = new Song();
        BeanUtils.copyProperties(request, song);
        
        // 设置默认状态
        if (song.getStatus() == null) {
            song.setStatus(1);
        }
        
        return songRepository.save(song);
    }
    
    /**
     * 更新歌曲
     */
    public Song updateSong(Long id, SongRequest request) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        
        BeanUtils.copyProperties(request, song, "id", "playCount", "createTime", "updateTime");
        return songRepository.save(song);
    }
    
    /**
     * 删除歌曲
     */
    public void deleteSong(Long id) {
        if (!songRepository.existsById(id)) {
            throw new RuntimeException("歌曲不存在");
        }
        songRepository.deleteById(id);
    }
    
    /**
     * 获取歌曲详情
     */
    public Song getSong(Long id) {
        return songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
    }
    
    /**
     * 获取所有歌曲（分页）
     */
    public Page<Song> getSongs(Pageable pageable) {
        return songRepository.findAll(pageable);
    }
    
    /**
     * 根据状态获取歌曲（分页）
     */
    public Page<Song> getSongsByStatus(Integer status, Pageable pageable) {
        return songRepository.findByStatus(status, pageable);
    }
    
    /**
     * 搜索歌曲
     */
    public Page<Song> searchSongs(String keyword, Pageable pageable) {
        return songRepository.searchByKeyword(keyword, pageable);
    }
    
    /**
     * 获取热门歌曲
     */
    public Page<Song> getHotSongs(Pageable pageable) {
        return songRepository.findByStatusOrderByPlayCountDesc(1, pageable);
    }
    
    /**
     * 获取最新歌曲
     */
    public Page<Song> getNewSongs(Pageable pageable) {
        return songRepository.findByStatusOrderByCreateTimeDesc(1, pageable);
    }
    
    /**
     * 根据歌手获取歌曲
     */
    public List<Song> getSongsByArtist(Long artistId) {
        return songRepository.findByArtistId(artistId);
    }
    
    /**
     * 增加播放次数
     */
    public void incrementPlayCount(Long id) {
        Song song = songRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌曲不存在"));
        song.setPlayCount(song.getPlayCount() + 1);
        songRepository.save(song);
    }
}

