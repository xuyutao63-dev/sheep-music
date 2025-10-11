package com.example.sheepmusic.service;

import com.example.sheepmusic.dto.ArtistRequest;
import com.example.sheepmusic.entity.Artist;
import com.example.sheepmusic.repository.ArtistRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 歌手服务类
 */
@Service
public class ArtistService {
    
    @Autowired
    private ArtistRepository artistRepository;
    
    /**
     * 创建歌手
     */
    public Artist createArtist(ArtistRequest request) {
        // 检查歌手名是否已存在
        if (artistRepository.findByName(request.getName()).isPresent()) {
            throw new RuntimeException("歌手名称已存在");
        }
        
        Artist artist = new Artist();
        BeanUtils.copyProperties(request, artist);
        return artistRepository.save(artist);
    }
    
    /**
     * 更新歌手
     */
    public Artist updateArtist(Long id, ArtistRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌手不存在"));
        
        // 如果修改了名称，检查新名称是否已存在
        if (!artist.getName().equals(request.getName())) {
            if (artistRepository.findByName(request.getName()).isPresent()) {
                throw new RuntimeException("歌手名称已存在");
            }
        }
        
        BeanUtils.copyProperties(request, artist, "id", "createTime", "updateTime");
        return artistRepository.save(artist);
    }
    
    /**
     * 删除歌手
     */
    public void deleteArtist(Long id) {
        if (!artistRepository.existsById(id)) {
            throw new RuntimeException("歌手不存在");
        }
        artistRepository.deleteById(id);
    }
    
    /**
     * 获取歌手详情
     */
    public Artist getArtist(Long id) {
        return artistRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("歌手不存在"));
    }
    
    /**
     * 获取所有歌手（分页）
     */
    public Page<Artist> getArtists(Pageable pageable) {
        return artistRepository.findAll(pageable);
    }
    
    /**
     * 获取所有歌手（不分页）
     */
    public List<Artist> getAllArtists() {
        return artistRepository.findAll();
    }
}

