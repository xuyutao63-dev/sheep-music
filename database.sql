-- 创建数据库
CREATE DATABASE IF NOT EXISTS sheep_music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE sheep_music;

-- 用户表会由JPA自动创建，这里提供参考SQL

/*
CREATE TABLE IF NOT EXISTS tb_user (
    id BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    username VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    password VARCHAR(255) NOT NULL COMMENT '密码（加密）',
    nickname VARCHAR(50) COMMENT '昵称',
    avatar VARCHAR(500) COMMENT '头像URL',
    email VARCHAR(100) COMMENT '邮箱',
    phone VARCHAR(20) COMMENT '手机号',
    gender INT COMMENT '性别：0-未知，1-男，2-女',
    signature VARCHAR(200) COMMENT '个性签名',
    status INT NOT NULL DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    INDEX idx_username (username),
    INDEX idx_email (email)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';
*/

-- 说明：
-- 由于使用了JPA的自动建表功能（ddl-auto: update），
-- 启动项目时会自动创建表结构，无需手动执行建表SQL。
-- 只需要确保数据库 sheep_music 已创建即可。

