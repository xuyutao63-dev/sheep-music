-- ================================================
-- 修复 artist_id 字段错误
-- ================================================
-- 错误信息：Field 'artist_id' doesn't have a default value
-- 原因：旧字段 artist_id 是 NOT NULL，但新代码不再使用它
-- 解决方案：删除旧字段
-- ================================================

USE sheep_music;

-- 方案1：直接删除旧字段（推荐）
-- ================================================
ALTER TABLE tb_song DROP COLUMN artist_id;
ALTER TABLE tb_song DROP COLUMN artist_name;

-- 验证字段是否已删除
SHOW COLUMNS FROM tb_song;

-- ================================================
-- 方案2（备选）：如果不想删除，可以改为允许 NULL
-- ================================================
-- 取消注释以下两行来使用方案2：
-- ALTER TABLE tb_song MODIFY COLUMN artist_id BIGINT NULL;
-- ALTER TABLE tb_song MODIFY COLUMN artist_name VARCHAR(100) NULL;

-- ================================================
-- 完成！
-- ================================================





