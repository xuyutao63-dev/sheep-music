-- 更新歌单封面策略：从网格布局改为单张封面（类似QQ音乐）

-- 1. 清空现有的封面数据（因为格式从JSON数组改为单个URL）
UPDATE tb_playlist SET cover = NULL WHERE cover IS NOT NULL;

-- 2. 验证清空结果
SELECT COUNT(*) as cleared_count FROM tb_playlist WHERE cover IS NULL;

-- 说明：
-- 旧策略：cover 字段存储JSON数组 ["url1", "url2", "url3", "url4"]，显示2x2网格
-- 新策略：cover 字段存储单个URL字符串，显示最新添加的歌曲封面（类似QQ音乐）
-- 
-- 系统会在以下操作时自动更新封面：
-- 1. 添加歌曲到歌单
-- 2. 从歌单移除歌曲
-- 
-- 封面将自动使用最新添加的歌曲封面

