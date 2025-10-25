-- ================================================
-- 多歌手功能数据迁移脚本
-- ================================================
-- 用途：将现有的单歌手数据迁移到多对多关系
-- 执行时机：在启动后端服务后（JPA 已自动创建 song_artist 表）
-- ================================================

USE sheep_music;

-- 1. 检查 song_artist 表是否存在
SELECT COUNT(*) as table_exists 
FROM information_schema.tables 
WHERE table_schema = 'sheep_music' 
AND table_name = 'song_artist';
-- 应该返回 1，如果返回 0，请先启动后端服务让 JPA 创建表

-- 2. 查看当前有多少歌曲需要迁移
SELECT COUNT(*) as songs_to_migrate 
FROM tb_song 
WHERE artistId IS NOT NULL;

-- 3. 执行迁移（将 artistId 关系迁移到 song_artist 表）
INSERT INTO song_artist (song_id, artist_id)
SELECT id, artistId 
FROM tb_song 
WHERE artistId IS NOT NULL
AND NOT EXISTS (
    SELECT 1 FROM song_artist 
    WHERE song_id = tb_song.id 
    AND artist_id = tb_song.artistId
);
-- 这会将所有现有的歌手关系添加到中间表

-- 4. 验证迁移结果
SELECT 
    s.id,
    s.title,
    GROUP_CONCAT(a.name SEPARATOR ' / ') as artists,
    s.artistName as old_artist_name
FROM tb_song s
LEFT JOIN song_artist sa ON s.id = sa.song_id
LEFT JOIN tb_artist a ON sa.artist_id = a.id
GROUP BY s.id
ORDER BY s.id
LIMIT 20;
-- 检查 artists 列和 old_artist_name 列是否匹配

-- 5. 统计迁移情况
SELECT 
    'Songs in tb_song' as item,
    COUNT(*) as count
FROM tb_song
UNION ALL
SELECT 
    'Relationships in song_artist' as item,
    COUNT(*) as count
FROM song_artist
UNION ALL
SELECT 
    'Songs with artists' as item,
    COUNT(DISTINCT song_id) as count
FROM song_artist;

-- ================================================
-- 可选：清理旧字段（在确认迁移成功后执行）
-- ================================================

-- ⚠️ 警告：执行以下语句前，请确保：
--   1. 已经重启后端服务并测试功能正常
--   2. 已经备份了数据库
--   3. 前端也已经更新并测试完成

-- 取消注释以下语句来删除旧字段：

-- ALTER TABLE tb_song DROP COLUMN artistId;
-- ALTER TABLE tb_song DROP COLUMN artistName;

-- ================================================
-- 回滚脚本（如果需要回退）
-- ================================================

-- 如果需要回退到单歌手模式：

-- 1. 恢复 artistId 和 artistName 字段（如果已删除）
-- ALTER TABLE tb_song ADD COLUMN artistId BIGINT;
-- ALTER TABLE tb_song ADD COLUMN artistName VARCHAR(100);

-- 2. 从 song_artist 表恢复数据（取第一个歌手）
-- UPDATE tb_song s
-- SET artistId = (
--     SELECT artist_id FROM song_artist 
--     WHERE song_id = s.id 
--     LIMIT 1
-- );

-- UPDATE tb_song s
-- SET artistName = (
--     SELECT a.name FROM song_artist sa
--     JOIN tb_artist a ON sa.artist_id = a.id
--     WHERE sa.song_id = s.id
--     LIMIT 1
-- );

-- 3. 删除 song_artist 表
-- DROP TABLE song_artist;

-- ================================================
-- 完成！
-- ================================================





