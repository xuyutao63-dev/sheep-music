package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.Artist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 歌手数据访问层
 */
@Repository
public interface ArtistRepository extends JpaRepository<Artist, Long> {
    
    /**
     * 根据歌手名称查找
     */
    Optional<Artist> findByName(String name);
}

