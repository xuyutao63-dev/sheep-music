-- ================================================
-- 安全迁移脚本 - 保留所有已有数据
-- ================================================
-- 目的：将旧的 artist_id 数据迁移到新的 song_artist 表
-- 然后安全删除旧字段
-- ================================================

USE sheep_music;

-- ================================================
-- 第1步：检查当前状态
-- ================================================

-- 1.1 查看有多少首歌曲
SELECT '歌曲总数' as item, COUNT(*) as count FROM tb_song;

-- 1.2 查看有多少首歌曲已经有歌手信息
SELECT '有歌手信息的歌曲数' as item, COUNT(*) as count 
FROM tb_song 
WHERE artist_id IS NOT NULL;

-- 1.3 查看 song_artist 表当前的关系数
SELECT 'song_artist表当前关系数' as item, COUNT(*) as count 
FROM song_artist;

-- ================================================
-- 第2步：数据迁移
-- ================================================

-- 2.1 将 artist_id 的数据迁移到 song_artist 表
-- 使用 INSERT IGNORE 避免重复插入
INSERT IGNORE INTO song_artist (song_id, artist_id)
SELECT id, artist_id 
FROM tb_song 
WHERE artist_id IS NOT NULL;

-- 2.2 查看迁移后的关系数
SELECT '迁移后 song_artist 关系数' as item, COUNT(*) as count 
FROM song_artist;

-- ================================================
-- 第3步：验证数据完整性
-- ================================================

-- 3.1 验证：每首歌都有对应的歌手关系
SELECT 
    '数据完整性检查' as check_name,
    CASE 
        WHEN COUNT(DISTINCT s.id) = COUNT(DISTINCT sa.song_id) 
        THEN '✅ 通过：所有歌曲都已迁移'
        ELSE '❌ 失败：有歌曲未迁移'
    END as result
FROM tb_song s
LEFT JOIN song_artist sa ON s.id = sa.song_id
WHERE s.artist_id IS NOT NULL;

-- 3.2 详细查看前10首歌的迁移情况
SELECT 
    s.id,
    s.title,
    s.artist_name as '旧歌手名称',
    GROUP_CONCAT(a.name SEPARATOR ' / ') as '新歌手名称',
    CASE 
        WHEN s.artist_name = GROUP_CONCAT(a.name SEPARATOR ' / ') 
        THEN '✅ 匹配' 
        ELSE '⚠️ 不匹配' 
    END as '验证结果'
FROM tb_song s
LEFT JOIN song_artist sa ON s.id = sa.song_id
LEFT JOIN tb_artist a ON sa.artist_id = a.id
WHERE s.artist_id IS NOT NULL
GROUP BY s.id
ORDER BY s.id
LIMIT 10;

-- 3.3 检查是否有歌曲在迁移后没有歌手
SELECT 
    s.id,
    s.title,
    s.artist_name as '原歌手名称'
FROM tb_song s
LEFT JOIN song_artist sa ON s.id = sa.song_id
WHERE s.artist_id IS NOT NULL
AND sa.song_id IS NULL;
-- 如果有结果，说明迁移失败，需要检查！

-- ================================================
-- 第4步：修改字段属性（允许 NULL）
-- ================================================
-- 先不删除字段，而是改为允许 NULL
-- 这样即使出问题也可以回滚

ALTER TABLE tb_song MODIFY COLUMN artist_id BIGINT NULL;
ALTER TABLE tb_song MODIFY COLUMN artist_name VARCHAR(100) NULL;

-- 验证字段已改为 NULL
SELECT 
    COLUMN_NAME,
    IS_NULLABLE,
    COLUMN_TYPE
FROM information_schema.COLUMNS
WHERE TABLE_SCHEMA = 'sheep_music'
AND TABLE_NAME = 'tb_song'
AND COLUMN_NAME IN ('artist_id', 'artist_name');
-- IS_NULLABLE 应该显示 YES

-- ================================================
-- 第5步：最终验证（重要！）
-- ================================================

-- 5.1 随机抽取10首歌验证
SELECT 
    s.id,
    s.title,
    s.artist_name as '旧数据',
    GROUP_CONCAT(a.name ORDER BY a.name SEPARATOR ' / ') as '新数据'
FROM tb_song s
LEFT JOIN song_artist sa ON s.id = sa.song_id
LEFT JOIN tb_artist a ON sa.artist_id = a.id
GROUP BY s.id
ORDER BY RAND()
LIMIT 10;

-- 5.2 统计报告
SELECT 
    '总歌曲数' as 统计项,
    COUNT(*) as 数量
FROM tb_song
UNION ALL
SELECT 
    '有旧歌手信息的歌曲数' as 统计项,
    COUNT(*) as 数量
FROM tb_song
WHERE artist_id IS NOT NULL
UNION ALL
SELECT 
    '新关系表中的关系数' as 统计项,
    COUNT(*) as 数量
FROM song_artist
UNION ALL
SELECT 
    '有新歌手关系的歌曲数' as 统计项,
    COUNT(DISTINCT song_id) as 数量
FROM song_artist;

-- ================================================
-- 第6步：测试新系统
-- ================================================
-- ⚠️ 现在重启后端服务，测试添加新歌曲
-- ⚠️ 如果测试成功，才执行第7步！
-- ================================================

-- 暂停：请重启后端并测试以下功能：
-- 1. ✅ 添加新歌曲（选择多个歌手）
-- 2. ✅ 编辑歌曲（修改歌手）
-- 3. ✅ 查看歌曲列表（显示多个歌手）
-- 4. ✅ 搜索功能
-- 5. ✅ 首页显示

-- ================================================
-- 第7步：删除旧字段（确认无误后再执行）
-- ================================================
-- ⚠️ 只有在第6步测试完全通过后，才执行这一步！
-- ⚠️ 取消下面两行的注释来删除旧字段：

-- ALTER TABLE tb_song DROP COLUMN artist_id;
-- ALTER TABLE tb_song DROP COLUMN artist_name;

-- ================================================
-- 回滚方案（如果出问题）
-- ================================================
-- 如果新系统有问题，可以用以下SQL回滚：

/*
-- 回滚步骤1：将字段改回 NOT NULL
ALTER TABLE tb_song MODIFY COLUMN artist_id BIGINT NOT NULL;
ALTER TABLE tb_song MODIFY COLUMN artist_name VARCHAR(100) NOT NULL;

-- 回滚步骤2：清空 song_artist 表
TRUNCATE TABLE song_artist;

-- 现在可以回到旧版本代码
*/

-- ================================================
-- 完成！
-- ================================================

-- 执行总结：
-- ✅ 第1-3步：数据迁移和验证
-- ✅ 第4步：字段改为允许 NULL（解决报错）
-- ✅ 第5步：全面验证
-- ⏸️  第6步：重启并测试
-- ⏸️  第7步：确认无误后删除旧字段

