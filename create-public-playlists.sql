-- 创建公开歌单测试数据
-- 此脚本用于为歌单广场功能创建测试数据

-- ========== 第一步：查询可用的用户ID ==========
-- 请先执行这个查询，查看系统中存在的用户
SELECT id, username, nickname FROM tb_user LIMIT 5;

-- ========== 第二步：修改下面的 @user_id 变量 ==========
-- 将 @user_id 设置为上面查询结果中的某个用户ID
SET @user_id = (SELECT MIN(id) FROM tb_user LIMIT 1);

-- 验证用户ID是否存在
SELECT @user_id AS '将使用的用户ID', 
       (SELECT nickname FROM tb_user WHERE id = @user_id) AS '用户昵称';

-- 如果上面显示 NULL，说明没有用户，请先创建用户！

-- ========== 第三步：创建公开歌单 ==========

-- 1. 流行歌单
INSERT INTO tb_playlist (name, description, category, is_public, user_id, play_count, collect_count, song_count, create_time, update_time)
VALUES 
('华语流行精选', '精选最热门的华语流行歌曲', '流行', TRUE, @user_id, 12580, 356, 50, NOW(), NOW()),
('欧美流行榜', '欧美最新流行音乐榜单', '流行', TRUE, @user_id, 8960, 245, 45, NOW(), NOW()),
('日韩流行热曲', '日韩最火的流行歌曲合集', '流行', TRUE, @user_id, 6780, 189, 40, NOW(), NOW());

-- 2. 摇滚歌单
INSERT INTO tb_playlist (name, description, category, is_public, user_id, play_count, collect_count, song_count, create_time, update_time)
VALUES 
('经典摇滚传奇', '永不过时的经典摇滚乐', '摇滚', TRUE, @user_id, 15670, 423, 60, NOW(), NOW()),
('独立摇滚精选', '独立音乐人的摇滚作品', '摇滚', TRUE, @user_id, 5430, 167, 35, NOW(), NOW());

-- 3. 民谣歌单
INSERT INTO tb_playlist (name, description, category, is_public, user_id, play_count, collect_count, song_count, create_time, update_time)
VALUES 
('民谣时光', '温暖治愈的民谣歌曲', '民谣', TRUE, @user_id, 9870, 312, 48, NOW(), NOW()),
('校园民谣回忆', '那些年我们听过的校园民谣', '民谣', TRUE, @user_id, 7650, 234, 42, NOW(), NOW());

-- 4. 电子音乐歌单
INSERT INTO tb_playlist (name, description, category, is_public, user_id, play_count, collect_count, song_count, create_time, update_time)
VALUES 
('电音狂欢', '嗨爆全场的电子音乐', '电子', TRUE, @user_id, 11230, 289, 55, NOW(), NOW()),
('氛围电子', '适合工作学习的氛围电子乐', '电子', TRUE, @user_id, 8450, 256, 38, NOW(), NOW());

-- 5. 纯音乐歌单
INSERT INTO tb_playlist (name, description, category, is_public, user_id, play_count, collect_count, song_count, create_time, update_time)
VALUES 
('钢琴轻音乐', '优美的钢琴纯音乐', '纯音乐', TRUE, @user_id, 13450, 378, 52, NOW(), NOW()),
('古典音乐精选', '经典的古典音乐作品', '纯音乐', TRUE, @user_id, 6890, 198, 44, NOW(), NOW());

-- 6. 其他类型歌单
INSERT INTO tb_playlist (name, description, category, is_public, user_id, play_count, collect_count, song_count, create_time, update_time)
VALUES 
('说唱精选', '最火的说唱歌曲合集', '其他', TRUE, @user_id, 10560, 298, 46, NOW(), NOW()),
('爵士乐精选', '经典爵士乐作品', '其他', TRUE, @user_id, 5670, 145, 36, NOW(), NOW()),
('影视金曲', '经典影视剧主题曲', '其他', TRUE, @user_id, 14230, 412, 58, NOW(), NOW());

-- 查询创建的歌单
SELECT 
    p.id,
    p.name,
    p.category,
    p.is_public,
    p.play_count,
    p.collect_count,
    p.song_count,
    u.nickname as creator_name
FROM tb_playlist p
LEFT JOIN tb_user u ON p.user_id = u.id
WHERE p.is_public = TRUE
ORDER BY p.create_time DESC;

-- 提示：
-- 1. 请确保 user_id 对应的用户在 tb_user 表中存在
-- 2. 如果需要为歌单添加歌曲，请使用 tb_playlist_song 表
-- 3. 歌单的 cover 字段会在添加歌曲后自动更新

