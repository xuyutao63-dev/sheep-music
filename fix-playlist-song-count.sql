-- 修复现有歌单的歌曲数量统计

-- 更新所有歌单的歌曲数量
UPDATE tb_playlist p
SET p.song_count = (
    SELECT COUNT(*) 
    FROM tb_playlist_song ps 
    WHERE ps.playlist_id = p.id
);

-- 验证结果
SELECT 
    p.id,
    p.name,
    p.song_count AS current_count,
    (SELECT COUNT(*) FROM tb_playlist_song ps WHERE ps.playlist_id = p.id) AS actual_count
FROM tb_playlist p
WHERE p.song_count != (SELECT COUNT(*) FROM tb_playlist_song ps WHERE ps.playlist_id = p.id);

-- 如果上面的查询没有返回结果，说明所有歌单的数量都已正确

