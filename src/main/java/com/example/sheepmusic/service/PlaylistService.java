package com.example.sheepmusic.service;

import com.example.sheepmusic.entity.Playlist;
import com.example.sheepmusic.entity.PlaylistSong;
import com.example.sheepmusic.entity.Song;
import com.example.sheepmusic.repository.PlaylistRepository;
import com.example.sheepmusic.repository.PlaylistSongRepository;
import com.example.sheepmusic.repository.SongRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 歌单服务类
 */
@Service
public class PlaylistService {
    
    @Autowired
    private PlaylistRepository playlistRepository;
    
    @Autowired
    private PlaylistSongRepository playlistSongRepository;
    
    @Autowired
    private SongRepository songRepository;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    /**
     * 创建歌单
     */
    @Transactional
    public Playlist createPlaylist(Playlist playlist) {
        // 设置默认值
        if (playlist.getIsPublic() == null) {
            playlist.setIsPublic(false);
        }
        if (playlist.getPlayCount() == null) {
            playlist.setPlayCount(0L);
        }
        if (playlist.getCollectCount() == null) {
            playlist.setCollectCount(0L);
        }
        if (playlist.getSongCount() == null) {
            playlist.setSongCount(0);
        }
        
        return playlistRepository.save(playlist);
    }
    
    /**
     * 更新歌单信息
     */
    @Transactional
    public Playlist updatePlaylist(Long playlistId, Playlist updatedInfo, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
        
        // 验证权限
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限编辑此歌单");
        }
        
        // 更新字段
        if (updatedInfo.getName() != null) {
            playlist.setName(updatedInfo.getName());
        }
        if (updatedInfo.getDescription() != null) {
            playlist.setDescription(updatedInfo.getDescription());
        }
        if (updatedInfo.getCover() != null) {
            playlist.setCover(updatedInfo.getCover());
        }
        if (updatedInfo.getCategory() != null) {
            playlist.setCategory(updatedInfo.getCategory());
        }
        if (updatedInfo.getIsPublic() != null) {
            playlist.setIsPublic(updatedInfo.getIsPublic());
        }
        
        return playlistRepository.save(playlist);
    }
    
    /**
     * 删除歌单
     */
    @Transactional
    public void deletePlaylist(Long playlistId, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
        
        // 验证权限
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限删除此歌单");
        }
        
        // 先使用原生SQL删除关联的歌曲（避免JPA状态冲突）
        playlistSongRepository.deleteByPlaylistId(playlistId);
        
        // 再删除歌单本身
        playlistRepository.delete(playlist);
    }
    
    /**
     * 获取歌单详情
     */
    public Playlist getPlaylistById(Long playlistId) {
        return playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
    }
    
    /**
     * 获取用户的歌单列表
     */
    public Page<Playlist> getUserPlaylists(Long userId, Pageable pageable) {
        return playlistRepository.findByUserIdOrderByCreateTimeDesc(userId, pageable);
    }
    
    /**
     * 获取用户的所有歌单（不分页）
     */
    public List<Playlist> getUserAllPlaylists(Long userId) {
        return playlistRepository.findByUserIdOrderByCreateTimeDesc(userId);
    }
    
    /**
     * 统计用户的歌单数量
     */
    public long countUserPlaylists(Long userId) {
        return playlistRepository.countByUserId(userId);
    }
    
    /**
     * 添加歌曲到歌单
     */
    @Transactional
    public PlaylistSong addSongToPlaylist(Long playlistId, Long songId, Long userId) {
        // 验证歌单
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
        
        // 验证权限
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限编辑此歌单");
        }
        
        // 验证歌曲
        if (!songRepository.existsById(songId)) {
            throw new RuntimeException("歌曲不存在");
        }
        
        // 检查是否已存在
        if (playlistSongRepository.existsByPlaylistIdAndSongId(playlistId, songId)) {
            throw new RuntimeException("歌曲已在歌单中");
        }
        
        // 创建关联
        PlaylistSong playlistSong = new PlaylistSong();
        playlistSong.setPlaylistId(playlistId);
        playlistSong.setSongId(songId);
        playlistSong.setSortOrder((int) playlistSongRepository.countByPlaylistId(playlistId));
        
        playlistSong = playlistSongRepository.save(playlistSong);
        
        // 更新歌单的歌曲数量和封面
        updatePlaylistInfo(playlistId);
        
        return playlistSong;
    }
    
    /**
     * 批量添加歌曲到歌单
     */
    @Transactional
    public void addSongsToPlaylist(Long playlistId, List<Long> songIds, Long userId) {
        // 验证歌单
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
        
        // 验证权限
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限编辑此歌单");
        }
        
        // 过滤已存在的歌曲
        List<Long> existingSongIds = playlistSongRepository.findByPlaylistIdOrderByAddTimeDesc(playlistId)
            .stream()
            .map(PlaylistSong::getSongId)
            .collect(Collectors.toList());
        
        List<Long> newSongIds = songIds.stream()
            .filter(id -> !existingSongIds.contains(id))
            .collect(Collectors.toList());
        
        // 批量插入
        int sortOrder = (int) playlistSongRepository.countByPlaylistId(playlistId);
        for (Long songId : newSongIds) {
            if (songRepository.existsById(songId)) {
                PlaylistSong playlistSong = new PlaylistSong();
                playlistSong.setPlaylistId(playlistId);
                playlistSong.setSongId(songId);
                playlistSong.setSortOrder(sortOrder++);
                playlistSongRepository.save(playlistSong);
            }
        }
        
        // 更新歌单的歌曲数量和封面
        updatePlaylistInfo(playlistId);
    }
    
    /**
     * 从歌单移除歌曲
     */
    @Transactional
    public void removeSongFromPlaylist(Long playlistId, Long songId, Long userId) {
        // 验证歌单
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
        
        // 验证权限
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限编辑此歌单");
        }
        
        // 删除关联
        playlistSongRepository.deleteByPlaylistIdAndSongId(playlistId, songId);
        
        // 更新歌单的歌曲数量和封面
        updatePlaylistInfo(playlistId);
    }
    
    /**
     * 获取歌单的歌曲列表
     */
    public List<PlaylistSong> getPlaylistSongs(Long playlistId) {
        return playlistSongRepository.findByPlaylistIdOrderByAddTimeDesc(playlistId);
    }
    
    /**
     * 分页获取歌单的歌曲列表
     */
    public Page<PlaylistSong> getPlaylistSongs(Long playlistId, Pageable pageable) {
        return playlistSongRepository.findByPlaylistIdOrderByAddTimeDesc(playlistId, pageable);
    }
    
    /**
     * 获取公开歌单列表
     */
    public Page<Playlist> getPublicPlaylists(Pageable pageable) {
        Page<Playlist> playlists = playlistRepository.findByIsPublicTrueOrderByCreateTimeDesc(pageable);
        // 触发 creator 的加载
        playlists.forEach(playlist -> {
            if (playlist.getCreator() != null) {
                playlist.getCreator().getNickname();
            }
        });
        return playlists;
    }
    
    /**
     * 按分类获取公开歌单
     */
    public Page<Playlist> getPublicPlaylistsByCategory(String category, Pageable pageable) {
        Page<Playlist> playlists = playlistRepository.findByIsPublicTrueAndCategoryOrderByCreateTimeDesc(category, pageable);
        // 触发 creator 的加载
        playlists.forEach(playlist -> {
            if (playlist.getCreator() != null) {
                playlist.getCreator().getNickname();
            }
        });
        return playlists;
    }
    
    /**
     * 获取热门公开歌单
     */
    public Page<Playlist> getHotPlaylists(Pageable pageable) {
        Page<Playlist> playlists = playlistRepository.findByIsPublicTrueOrderByPlayCountDesc(pageable);
        // 触发 creator 的加载
        playlists.forEach(playlist -> {
            if (playlist.getCreator() != null) {
                playlist.getCreator().getNickname();
            }
        });
        return playlists;
    }
    
    /**
     * 搜索公开歌单
     */
    public Page<Playlist> searchPublicPlaylists(String keyword, Pageable pageable) {
        return playlistRepository.searchPublicPlaylistsByName(keyword, pageable);
    }
    
    /**
     * 增加播放次数
     */
    @Transactional
    public void incrementPlayCount(Long playlistId) {
        playlistRepository.incrementPlayCount(playlistId);
    }
    
    /**
     * 设置歌单公开/私有
     */
    @Transactional
    public void setPlaylistPublic(Long playlistId, boolean isPublic, Long userId) {
        Playlist playlist = playlistRepository.findById(playlistId)
            .orElseThrow(() -> new RuntimeException("歌单不存在"));
        
        // 验证权限
        if (!playlist.getUserId().equals(userId)) {
            throw new RuntimeException("无权限修改此歌单");
        }
        
        playlist.setIsPublic(isPublic);
        playlistRepository.save(playlist);
    }
    
    /**
     * 更新歌单信息（歌曲数量和封面）
     * 封面策略：使用最新添加的歌曲封面（类似QQ音乐）
     */
    @Transactional
    public void updatePlaylistInfo(Long playlistId) {
        try {
            // 获取歌单
            Playlist playlist = playlistRepository.findById(playlistId).orElse(null);
            if (playlist == null) {
                return;
            }
            
            // 更新歌曲数量
            long count = playlistSongRepository.countByPlaylistId(playlistId);
            playlist.setSongCount((int) count);
            
            // 更新封面：使用最新添加的歌曲封面
            List<PlaylistSong> playlistSongs = playlistSongRepository.findByPlaylistIdOrderByAddTimeDesc(playlistId);
            String coverUrl = null;
            
            if (!playlistSongs.isEmpty()) {
                // 获取最新添加的歌曲
                Long songId = playlistSongs.get(0).getSongId();
                Song song = songRepository.findById(songId).orElse(null);
                if (song != null && song.getCover() != null) {
                    coverUrl = song.getCover();
                }
            }
            
            // 直接存储单个URL字符串（而不是JSON数组）
            playlist.setCover(coverUrl);
            
            // 一次性保存
            playlistRepository.save(playlist);
        } catch (Exception e) {
            // 更新失败不影响主流程
            e.printStackTrace();
        }
    }
}

