-- =============================================
-- Sheep Music 数据库初始化脚本
-- =============================================

-- 1. 创建数据库（如果不存在）
CREATE DATABASE IF NOT EXISTS `sheepmusic` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_general_ci;

-- 2. 使用数据库
USE `sheepmusic`;

-- =============================================
-- 注意：
-- 表结构由 Spring Boot JPA 自动创建（hibernate.ddl-auto: update）
-- 以下是表结构参考，实际运行时会自动生成
-- =============================================

-- 用户表（tb_user）
-- JPA 会自动创建，包含字段：
-- id, username, password, nickname, avatar, email, phone, gender, signature, role, status, create_time, update_time

-- 歌手表（tb_artist）
-- JPA 会自动创建，包含字段：
-- id, name, avatar, description, region, create_time, update_time

-- 专辑表（tb_album）
-- JPA 会自动创建，包含字段：
-- id, name, cover, description, release_date, create_time, update_time

-- 歌曲表（tb_song）
-- JPA 会自动创建，包含字段：
-- id, title, artist_id, artist_name, album_id, album_name, duration, cover, url, lyric, play_count, release_time, status, create_time, update_time

-- =============================================
-- 初始化管理员账号（密码：123456）
-- =============================================
-- 注意：如果已存在 admin 用户，下面的 SQL 会报错（重复插入），这是正常的
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `role`, `status`, `create_time`, `update_time`) 
VALUES ('admin', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', '管理员', 'admin', 1, NOW(), NOW())
ON DUPLICATE KEY UPDATE `username` = `username`;  -- 如果已存在，不做任何操作

-- =============================================
-- 说明：
-- 1. 首次运行项目时，JPA 会自动创建所有表
-- 2. 管理员账号会在项目启动后自动创建（如果不存在）
-- 3. 你也可以手动运行上面的 INSERT 语句创建管理员账号
-- =============================================

