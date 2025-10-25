-- 测试数据脚本
-- 请先确保后端服务器已运行一次，让 JPA 自动创建表结构
-- 然后执行此脚本插入测试数据

USE sheepmusic;

-- 1. 插入测试歌手数据
INSERT INTO tb_artist (id, name, description, avatar, status, create_time, update_time) VALUES
(1, '周杰伦', '华语流行天王', 'https://via.placeholder.com/200?text=Jay', 1, NOW(), NOW()),
(2, '林俊杰', '新加坡流行歌手', 'https://via.placeholder.com/200?text=JJ', 1, NOW(), NOW()),
(3, '邓紫棋', 'G.E.M.', 'https://via.placeholder.com/200?text=GEM', 1, NOW(), NOW()),
(4, '五月天', '台湾摇滚乐团', 'https://via.placeholder.com/200?text=Mayday', 1, NOW(), NOW()),
(5, '薛之谦', '内地歌手', 'https://via.placeholder.com/200?text=Joker', 1, NOW(), NOW());

-- 2. 插入测试歌曲数据（带歌词）
INSERT INTO tb_song (title, artist_id, artist_name, duration, cover, url, lyric, play_count, release_time, status, create_time, update_time) VALUES
('七里香', 1, '周杰伦', 298, 'https://via.placeholder.com/300?text=Jay1', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-1.mp3', 
'[00:00.50]周杰伦 - 七里香
[00:12.50]窗外的麻雀在电线杆上多嘴
[00:18.30]你说这一句很有夏天的感觉
[00:25.80]手中的铅笔在纸上来来回回
[00:31.50]我用几行字形容你是我的谁
[00:38.20]秋刀鱼的滋味猫跟你都想了解
[00:44.80]初恋的香味就这样被我们寻回', 
15230, NOW(), 1, NOW(), NOW()),

('稻香', 1, '周杰伦', 223, 'https://via.placeholder.com/300?text=Jay2', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-2.mp3',
'[00:00.50]周杰伦 - 稻香
[00:08.20]对这个世界如果你有太多的抱怨
[00:12.80]跌倒了就不敢继续往前走
[00:16.50]为什么人要这么的脆弱 堕落
[00:23.20]请你打开电视看看多少人为生命在努力勇敢的走下去
[00:30.80]我们是不是该知足珍惜一切就算没有拥有',
23450, NOW(), 1, NOW(), NOW()),

('修炼爱情', 1, '周杰伦', 267, 'https://via.placeholder.com/300?text=Jay3', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-3.mp3',
'[00:00.50]周杰伦 - 修炼爱情
[00:18.30]我闭上眼睛就是天黑
[00:23.50]在爱情面前讨价还价
[00:27.80]我也只能这样了
[00:32.20]你说你想要逃 却永远逃不掉',
18920, NOW(), 1, NOW(), NOW()),

('江南', 2, '林俊杰', 263, 'https://via.placeholder.com/300?text=JJ1', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-4.mp3',
'[00:00.50]林俊杰 - 江南
[00:15.30]风到这里就是粘
[00:18.80]粘住过客的思念
[00:22.50]雨到了这里缠成线
[00:26.20]缠着我们流连人世间
[00:30.80]你在身边就是缘
[00:34.50]缘分写在三生石上面',
29850, NOW(), 1, NOW(), NOW()),

('她说', 2, '林俊杰', 276, 'https://via.placeholder.com/300?text=JJ2', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-5.mp3',
'[00:00.50]林俊杰 - 她说
[00:12.30]他静悄悄地来过
[00:16.80]他慢慢带走沉默
[00:20.50]只是最后的承诺
[00:24.20]还是没有带走了寂寞',
21340, NOW(), 1, NOW(), NOW()),

('泡沫', 3, '邓紫棋', 243, 'https://via.placeholder.com/300?text=GEM1', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-6.mp3',
'[00:00.50]邓紫棋 - 泡沫
[00:18.20]泡沫美丽
[00:21.50]尽管一刹花火
[00:25.80]你所有承诺
[00:29.20]虽然都太脆弱
[00:32.80]但爱像泡沫
[00:36.50]如果能够看破',
32100, NOW(), 1, NOW(), NOW()),

('光年之外', 3, '邓紫棋', 287, 'https://via.placeholder.com/300?text=GEM2', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-7.mp3',
'[00:00.50]邓紫棋 - 光年之外
[00:15.30]感受停在我发端的指尖
[00:20.80]如何瞬间冻结时间
[00:25.50]记住望着我坚定的双眼
[00:30.20]也许已经没有明天',
27680, NOW(), 1, NOW(), NOW()),

('倔强', 4, '五月天', 267, 'https://via.placeholder.com/300?text=Mayday1', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-8.mp3',
'[00:00.50]五月天 - 倔强
[00:22.30]当我和世界不一样
[00:26.80]那就让我不一样
[00:30.50]坚持对我来说就是以刚克刚
[00:35.20]我如果对自己不行如果对自己说谎',
35420, NOW(), 1, NOW(), NOW()),

('演员', 5, '薛之谦', 269, 'https://via.placeholder.com/300?text=Joker1', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-9.mp3',
'[00:00.50]薛之谦 - 演员
[00:18.30]简单点说话的方式简单点
[00:23.80]递进的情绪请省略
[00:27.50]你又不是个演员
[00:31.20]别设计那些情节',
41250, NOW(), 1, NOW(), NOW()),

('刚刚好', 5, '薛之谦', 253, 'https://via.placeholder.com/300?text=Joker2', 'https://www.soundhelix.com/examples/mp3/SoundHelix-Song-10.mp3',
'[00:00.50]薛之谦 - 刚刚好
[00:15.30]我用什么把你留住
[00:20.80]靠脑海里你的模样刚刚好
[00:26.50]不谈寂寞太孤独
[00:31.20]不谈未来太沉重',
19870, NOW(), 1, NOW(), NOW());

-- 显示插入结果
SELECT '测试数据插入完成！' AS status;
SELECT COUNT(*) AS artist_count FROM tb_artist;
SELECT COUNT(*) AS song_count FROM tb_song;








