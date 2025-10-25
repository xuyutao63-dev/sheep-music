-- ============================================
-- 歌单功能数据库表
-- ============================================

-- 1. 歌单表
CREATE TABLE IF NOT EXISTS tb_playlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '歌单名称',
    description TEXT COMMENT '歌单描述',
    cover VARCHAR(500) COMMENT '封面图片URL',
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    is_public TINYINT DEFAULT 0 COMMENT '是否公开 0-私有 1-公开',
    category VARCHAR(50) COMMENT '分类（流行/摇滚/民谣/电子/纯音乐等）',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    collect_count BIGINT DEFAULT 0 COMMENT '收藏次数',
    song_count INT DEFAULT 0 COMMENT '歌曲数量',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_user_id (user_id),
    INDEX idx_is_public (is_public),
    INDEX idx_category (category),
    INDEX idx_create_time (create_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单表';

-- 2. 歌单-歌曲关联表
CREATE TABLE IF NOT EXISTS tb_playlist_song (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    playlist_id BIGINT NOT NULL COMMENT '歌单ID',
    song_id BIGINT NOT NULL COMMENT '歌曲ID',
    add_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '添加时间',
    sort_order INT DEFAULT 0 COMMENT '排序序号（手动调整顺序）',
    UNIQUE KEY uk_playlist_song (playlist_id, song_id),
    INDEX idx_playlist_id (playlist_id),
    INDEX idx_song_id (song_id),
    INDEX idx_add_time (add_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='歌单-歌曲关联表';

-- 3. 歌单收藏表（可选，用于用户收藏他人歌单）
CREATE TABLE IF NOT EXISTS tb_playlist_collect (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL COMMENT '用户ID',
    playlist_id BIGINT NOT NULL COMMENT '歌单ID',
    collect_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY uk_user_playlist (user_id, playlist_id),
    INDEX idx_user_id (user_id),
    INDEX idx_playlist_id (playlist_id),
    INDEX idx_collect_time (collect_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户收藏歌单表';

