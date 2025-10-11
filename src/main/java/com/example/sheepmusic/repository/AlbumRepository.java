package com.example.sheepmusic.repository;

import com.example.sheepmusic.entity.Album;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * 专辑数据访问层
 */
@Repository
public interface AlbumRepository extends JpaRepository<Album, Long> {
    
    /**
     * 根据专辑名称查找
     */
    Optional<Album> findByName(String name);
}

