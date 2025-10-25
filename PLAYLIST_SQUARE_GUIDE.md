# 歌单广场功能完善指南

## 📋 功能概述

本次更新完善了歌单广场的"推荐歌单"和"热门歌单"两个板块，集成了个性化推荐系统。

## ✨ 新增功能

### 1. 推荐歌单板块
- **个性化推荐**：登录用户在第一页会看到基于其收听习惯的个性化推荐歌单
- **公开歌单列表**：未登录用户或翻页后显示所有公开歌单
- **分类筛选**：支持按流行、摇滚、民谣、电子、纯音乐等分类筛选
- **个性化标签**：当使用个性化推荐时，会显示"个性化"标签

### 2. 热门歌单板块
- **按播放量排序**：展示播放量最高的公开歌单
- **分页支持**：支持分页浏览更多热门歌单
- **实时数据**：显示歌单的歌曲数量、播放次数等统计信息

### 3. 创建者信息
- **显示创建者昵称**：每个歌单都会显示创建者的昵称
- **懒加载优化**：后端自动触发创建者信息的加载

## 🔧 技术实现

### 前端修改

#### `front/sheep-music/src/views/Playlist.vue`
- 集成了 `getRecommendedPlaylists` API（来自推荐系统）
- 添加了登录状态判断，为登录用户提供个性化推荐
- 优化了空状态提示，引导未登录用户登录
- 添加了"个性化"标签显示

### 后端修改

#### `back/music-project/src/main/java/com/example/sheepmusic/service/PlaylistService.java`
- 优化了 `getPublicPlaylists()`、`getPublicPlaylistsByCategory()`、`getHotPlaylists()` 方法
- 添加了创建者信息的懒加载触发逻辑

#### `back/music-project/src/main/java/com/example/sheepmusic/service/RecommendationService.java`
- 优化了 `getRecommendedPlaylists()` 方法
- 添加了创建者信息的懒加载触发逻辑

#### `back/music-project/src/main/java/com/example/sheepmusic/repository/PlaylistRepository.java`
- 添加了 `findByIsPublicOrderByCreateTimeDesc(boolean)` 方法

## 📊 推荐算法说明

### 推荐歌单算法
1. **用户画像构建**：基于用户的收藏和播放历史（播放次数≥3）
2. **冷启动处理**：无历史数据的用户返回热门歌单
3. **歌单评分**：
   - 排除用户自己创建的歌单
   - 使用对数函数平滑歌曲数量的影响
   - 公式：`score = (songCount > 0 ? 1.0 : 0.0) * (1 + log(1 + songCount))`
4. **排序返回**：按评分降序返回Top N

### 热门歌单算法
- 按播放量降序排序
- 只展示公开歌单
- 支持分页

## 🧪 测试步骤

### 1. 准备测试数据

执行 SQL 脚本创建公开歌单：

```bash
mysql -u your_username -p your_database < back/music-project/create-public-playlists.sql
```

**注意**：请先修改脚本中的 `user_id`，使用你系统中存在的用户ID。

### 2. 启动服务

**后端**：
```bash
cd back/music-project
mvn spring-boot:run
```

**前端**：
```bash
cd front/sheep-music
npm run serve
```

### 3. 测试场景

#### 场景1：未登录用户访问
1. 访问 http://localhost:8080（或你的前端地址）
2. 点击导航栏的"歌单广场"
3. **预期结果**：
   - 推荐歌单：显示所有公开歌单（按创建时间倒序）
   - 热门歌单：显示播放量最高的歌单
   - 空状态：如果没有数据，显示"登录查看个性化推荐"按钮

#### 场景2：登录用户访问（有历史数据）
1. 登录系统
2. 确保该用户有一些收藏或播放历史
3. 访问"歌单广场"
4. **预期结果**：
   - 推荐歌单第一页：显示"个性化"标签，展示个性化推荐的歌单
   - 切换分类或翻页后：显示普通公开歌单列表
   - 热门歌单：显示播放量最高的歌单

#### 场景3：登录用户访问（无历史数据）
1. 登录一个新用户（无收藏和播放历史）
2. 访问"歌单广场"
3. **预期结果**：
   - 推荐歌单：显示热门歌单（冷启动策略）
   - 不显示"个性化"标签

#### 场景4：分类筛选
1. 在"推荐歌单"标签页
2. 点击不同的分类（流行、摇滚、民谣等）
3. **预期结果**：
   - 只显示对应分类的公开歌单
   - "个性化"标签消失（因为使用了分类筛选）

#### 场景5：查看歌单详情
1. 点击任意歌单卡片
2. **预期结果**：
   - 跳转到歌单详情页
   - 显示歌单中的所有歌曲
   - 显示创建者信息

### 4. 验证点

- [ ] 推荐歌单能正常加载
- [ ] 热门歌单能正常加载
- [ ] 创建者昵称正确显示
- [ ] 歌单封面正确显示
- [ ] 播放次数、歌曲数量等统计信息正确
- [ ] 分类筛选功能正常
- [ ] 分页功能正常
- [ ] 个性化推荐对登录用户生效
- [ ] 点击歌单能跳转到详情页
- [ ] 空状态提示正确显示

## 🐛 常见问题

### 问题1：推荐歌单为空
**原因**：数据库中没有公开歌单
**解决**：执行 `create-public-playlists.sql` 脚本创建测试数据

### 问题2：创建者昵称显示为"未知"
**原因**：歌单的 `user_id` 对应的用户不存在
**解决**：修改 SQL 脚本中的 `user_id`，使用系统中存在的用户ID

### 问题3：个性化推荐不生效
**原因**：
1. 用户未登录
2. 用户没有收藏或播放历史
3. 数据库中没有足够的公开歌单

**解决**：
1. 确保用户已登录
2. 让用户收藏一些歌曲或播放一些歌曲（播放次数≥3）
3. 创建更多公开歌单

### 问题4：热门歌单顺序不对
**原因**：歌单的 `play_count` 字段没有正确更新
**解决**：手动更新测试数据的 `play_count` 字段，或通过播放歌单来增加播放次数

### 问题5：歌单封面不显示
**原因**：
1. 歌单的 `cover` 字段为空
2. 歌单中没有歌曲

**解决**：
1. 为歌单添加歌曲（使用 `tb_playlist_song` 表）
2. 系统会自动使用最新添加的歌曲封面作为歌单封面

## 📝 数据库表结构

### tb_playlist（歌单表）
```sql
CREATE TABLE tb_playlist (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(100) NOT NULL COMMENT '歌单名称',
    description TEXT COMMENT '歌单描述',
    cover TEXT COMMENT '封面URL',
    user_id BIGINT NOT NULL COMMENT '创建者ID',
    is_public BOOLEAN DEFAULT FALSE COMMENT '是否公开',
    category VARCHAR(50) COMMENT '分类',
    play_count BIGINT DEFAULT 0 COMMENT '播放次数',
    collect_count BIGINT DEFAULT 0 COMMENT '收藏次数',
    song_count INT DEFAULT 0 COMMENT '歌曲数量',
    create_time DATETIME COMMENT '创建时间',
    update_time DATETIME COMMENT '更新时间',
    FOREIGN KEY (user_id) REFERENCES tb_user(id)
);
```

## 🎯 后续优化建议

1. **更精准的推荐算法**：
   - 分析歌单中的歌曲与用户喜好的匹配度
   - 考虑歌曲的类型、语言等标签

2. **社交功能**：
   - 显示好友创建的歌单
   - 显示好友收藏的歌单

3. **搜索功能**：
   - 在歌单广场添加搜索框
   - 支持按歌单名称、创建者搜索

4. **收藏功能**：
   - 允许用户收藏喜欢的歌单
   - 在"我的音乐"中显示收藏的歌单

5. **评论和评分**：
   - 允许用户对歌单进行评论
   - 允许用户对歌单进行评分

6. **标签系统**：
   - 为歌单添加标签（如"适合运动"、"适合学习"等）
   - 支持按标签筛选歌单

## 📚 相关文档

- [推荐系统实现指南](./RECOMMENDATION_SYSTEM_GUIDE.md)（如果有）
- [歌单功能文档](./PLAYLIST_FEATURE_GUIDE.md)（如果有）
- [API 文档](./API_DOCUMENTATION.md)（如果有）

## 🔗 API 端点

### 推荐歌单
- **GET** `/music/recommend/playlists?limit=20`
- **需要登录**：是
- **返回**：个性化推荐的歌单列表

### 公开歌单列表
- **GET** `/api/playlist/square?page=0&size=20&category=流行`
- **需要登录**：否
- **返回**：公开歌单分页列表

### 热门歌单
- **GET** `/api/playlist/square/hot?page=0&size=20`
- **需要登录**：否
- **返回**：热门歌单分页列表

---

**更新日期**：2025-10-25
**版本**：v1.0

