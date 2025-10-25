# 💖 收藏功能开发文档

## 📋 功能概述

实现了完整的音乐收藏功能，用户可以收藏喜欢的歌曲，并在"我的收藏"页面查看和管理。

---

## 🎯 功能列表

### 用户功能
- ✅ 收藏/取消收藏歌曲
- ✅ 查看我的收藏列表
- ✅ 批量检查收藏状态
- ✅ 统计收藏数量
- ✅ 收藏列表分页
- ✅ 从收藏列表播放歌曲

### 技术特性
- ✅ 数据库唯一性约束（避免重复收藏）
- ✅ 批量查询优化（一次请求检查多首歌曲）
- ✅ 懒加载歌曲信息（EAGER fetch）
- ✅ 收藏时间记录
- ✅ 用户权限验证

---

## 📦 后端实现

### 1. 数据库表结构

```sql
CREATE TABLE tb_favorite (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    user_id BIGINT NOT NULL,
    song_id BIGINT NOT NULL,
    create_time DATETIME NOT NULL,
    UNIQUE KEY uk_user_song (user_id, song_id),
    FOREIGN KEY (song_id) REFERENCES tb_song(id),
    INDEX idx_user_id (user_id)
);
```

**字段说明：**
- `id`: 主键
- `user_id`: 用户ID
- `song_id`: 歌曲ID
- `create_time`: 收藏时间
- `UNIQUE KEY`: 确保同一用户不能重复收藏同一首歌

### 2. 实体类 (Favorite.java)

```java
@Entity
@Table(name = "tb_favorite", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"user_id", "song_id"})
})
public class Favorite {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "user_id", nullable = false)
    private Long userId;
    
    @Column(name = "song_id", nullable = false)
    private Long songId;
    
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "song_id", insertable = false, updatable = false)
    private Song song;  // 关联歌曲信息
    
    @CreationTimestamp
    @Column(name = "create_time", nullable = false, updatable = false)
    private LocalDateTime createTime;
}
```

### 3. API 接口

| 方法 | 路径 | 说明 |
|------|------|------|
| POST | `/api/user/favorite/{songId}` | 添加收藏 |
| DELETE | `/api/user/favorite/{songId}` | 取消收藏 |
| POST | `/api/user/favorite/toggle/{songId}` | 切换收藏状态 |
| GET | `/api/user/favorite/check/{songId}` | 检查是否已收藏 |
| POST | `/api/user/favorite/batch-check` | 批量检查收藏状态 |
| GET | `/api/user/favorite/list` | 获取我的收藏列表 |
| GET | `/api/user/favorite/count` | 统计我的收藏数量 |

---

## 🎨 前端实现

### 1. API 封装 (favorite.js)

```javascript
// 切换收藏状态
export const toggleFavorite = (songId) => {
  return request({
    url: `/api/user/favorite/toggle/${songId}`,
    method: 'post'
  })
}

// 批量检查收藏状态
export const batchCheckFavorites = (songIds) => {
  return request({
    url: '/api/user/favorite/batch-check',
    method: 'post',
    data: songIds
  })
}

// 获取我的收藏列表
export const getMyFavorites = (params) => {
  return request({
    url: '/api/user/favorite/list',
    method: 'get',
    params
  })
}
```

### 2. 首页集成 (Home.vue)

**收藏按钮：**
```vue
<el-button 
  :icon="favoriteSongs[song.id] ? 'StarFilled' : 'Star'" 
  circle 
  size="small" 
  :type="favoriteSongs[song.id] ? 'danger' : ''"
  @click.stop="handleToggleFavorite(song.id)" 
  title="收藏"
/>
```

**收藏逻辑：**
```javascript
// 批量检查收藏状态
const loadFavoriteStatus = async () => {
  const songIds = [...hotSongs.value, ...newSongs.value].map(s => s.id)
  if (songIds.length > 0) {
    const res = await batchCheckFavorites(songIds)
    favoriteSongs.value = res.data || {}
  }
}

// 切换收藏
const handleToggleFavorite = async (songId) => {
  const res = await toggleFavorite(songId)
  favoriteSongs.value[songId] = res.data.isFavorite
  ElMessage.success(res.data.isFavorite ? '收藏成功' : '取消收藏成功')
}
```

### 3. 我的收藏页面 (MyFavorites.vue)

**路由：** `/my-favorites`

**功能：**
- 显示收藏列表
- 播放收藏的歌曲
- 取消收藏
- 分页加载

**UI 特性：**
- 显示封面、歌曲名、歌手、时长、收藏时间
- 鼠标悬停显示操作按钮
- 支持点击播放
- 响应式设计

---

## 🚀 使用指南

### 管理员/开发者

#### 1. 数据库初始化

```bash
# 启动后端服务，JPA 自动创建表
cd back/music-project
mvn spring-boot:run
```

JPA 会自动创建 `tb_favorite` 表。

#### 2. 验证表创建

```sql
-- 检查表是否创建
SHOW TABLES LIKE 'tb_favorite';

-- 查看表结构
DESC tb_favorite;

-- 查看索引
SHOW INDEX FROM tb_favorite;
```

### 普通用户

#### 1. 收藏歌曲

**方式1：在首页/搜索页**
- 找到喜欢的歌曲
- 点击 ⭐ 收藏按钮
- 按钮变红，显示"收藏成功"

**方式2：在播放器**
- （待实现）点击播放器的收藏按钮

#### 2. 查看收藏

- 访问 `/my-favorites` 页面
- 或点击导航栏的"我的收藏"
- 查看所有收藏的歌曲

#### 3. 取消收藏

**方式1：在歌曲列表**
- 点击红色的 ⭐ 按钮
- 按钮变为空心，显示"取消收藏成功"

**方式2：在收藏页面**
- 鼠标悬停在歌曲上
- 点击 🗑️ 删除按钮
- 确认后移除收藏

---

## 📊 数据统计

### 查询示例

```sql
-- 统计某用户的收藏数量
SELECT COUNT(*) FROM tb_favorite WHERE user_id = 1;

-- 查询最受欢迎的歌曲（被收藏最多）
SELECT 
    song_id, 
    COUNT(*) as favorite_count 
FROM tb_favorite 
GROUP BY song_id 
ORDER BY favorite_count DESC 
LIMIT 10;

-- 查询用户最近收藏的歌曲
SELECT 
    f.*, 
    s.title, 
    s.cover 
FROM tb_favorite f
JOIN tb_song s ON f.song_id = s.id
WHERE f.user_id = 1
ORDER BY f.create_time DESC
LIMIT 20;
```

---

## 🔧 性能优化

### 1. 批量查询优化

**问题：** 在歌曲列表页面，逐个检查每首歌是否收藏会产生N次查询。

**解决方案：** 使用批量查询API
```javascript
// ❌ 低效：N次请求
for (let song of songs) {
  const isFavorite = await checkFavorite(song.id)
}

// ✅ 高效：1次请求
const songIds = songs.map(s => s.id)
const favorites = await batchCheckFavorites(songIds)
```

### 2. EAGER 加载

```java
@ManyToOne(fetch = FetchType.EAGER)
private Song song;
```

在查询收藏列表时，自动加载关联的歌曲信息，避免N+1查询问题。

### 3. 数据库索引

```sql
-- 用户ID索引（查询某用户的收藏）
INDEX idx_user_id (user_id)

-- 唯一索引（防止重复收藏）
UNIQUE KEY uk_user_song (user_id, song_id)
```

---

## 🐛 常见问题

### Q1: 重复收藏报错

**问题：** 多次点击收藏按钮报错："已经收藏过该歌曲"

**原因：** 数据库唯一性约束。

**解决方案：** 
1. 使用 `toggleFavorite` API 而不是 `addFavorite`
2. 前端检查收藏状态后再发送请求

### Q2: 收藏状态不同步

**问题：** 收藏后，其他页面的收藏状态没有更新。

**原因：** 各页面独立维护收藏状态。

**解决方案：** 
1. 使用全局状态管理（Pinia store）
2. 或在每次进入页面时重新加载收藏状态

### Q3: 查询收藏列表慢

**问题：** 收藏歌曲很多时，查询很慢。

**解决方案：**
1. 使用分页（默认每页20条）
2. 添加数据库索引
3. 前端虚拟滚动（收藏超过1000首时）

---

## 🎯 未来优化

### 功能增强
- [ ] 收藏歌手
- [ ] 收藏专辑
- [ ] 收藏歌单
- [ ] 导出收藏列表
- [ ] 收藏分组/标签
- [ ] 收藏排序（按时间、歌曲名、歌手名）

### 性能优化
- [ ] 收藏状态全局状态管理
- [ ] Redis 缓存热门收藏数据
- [ ] 虚拟滚动（大列表）

### 用户体验
- [ ] 收藏动画效果
- [ ] 收藏推荐（基于收藏的推荐）
- [ ] 分享我的收藏
- [ ] 收藏统计图表

---

## 📝 版本历史

**v1.0 - 2025-10-19**
- ✅ 初始版本
- ✅ 基本收藏功能
- ✅ 我的收藏页面
- ✅ 首页集成收藏按钮

---

## 🎉 总结

收藏功能已完整实现，包括：
- ✅ 完整的后端API
- ✅ 数据库设计和索引优化
- ✅ 前端UI集成
- ✅ 我的收藏独立页面
- ✅ 批量查询优化

用户现在可以：
- 在首页收藏喜欢的歌曲
- 查看和管理收藏列表
- 从收藏列表播放歌曲

**更新时间**: 2025-10-19  
**版本**: v1.0 - 收藏功能




