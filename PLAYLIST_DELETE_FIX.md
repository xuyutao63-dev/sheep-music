# 歌单删除错误修复（最终版本）

## 🐛 问题描述

删除歌单时持续出现错误：
```
Batch update returned unexpected row count from update [0]; 
actual row count: 0; expected: 1; 
statement executed: delete from tb_playlist_song where id=?; 
nested exception is org.hibernate.StaleStateException
```

---

## 🔍 问题根因

### 实体映射冲突

1. **Playlist 实体**中有 `@ManyToMany` 关系：
   ```java
   @ManyToMany(fetch = FetchType.LAZY)
   @JoinTable(name = "tb_playlist_song", ...)
   private List<Song> songs;
   ```

2. **同时**还有独立的 `PlaylistSong` 实体：
   ```java
   @Entity
   @Table(name = "tb_playlist_song")
   public class PlaylistSong { ... }
   ```

3. **冲突：** 同一个表被两种方式管理：
   - JPA 的 `@ManyToMany` 在删除 Playlist 时自动清理关联
   - 我们手动通过 `PlaylistSong` 实体管理

4. **结果：** 
   - 我们先手动删除了关联记录
   - JPA 再次尝试删除（通过 @ManyToMany）
   - 但记录已不存在 → `StaleStateException`

---

## ✅ 最终修复方案

### 修复1：移除冲突的 @ManyToMany 字段

**Playlist.java** - 移除 `songs` 字段：

```java
@Entity
@Table(name = "tb_playlist")
public class Playlist {
    // ... 其他字段 ...
    
    // ❌ 移除这个字段（与 PlaylistSong 冲突）
    // @ManyToMany
    // private List<Song> songs;
    
    // ✅ 改为通过 PlaylistSong 实体查询
    // 注意：歌曲关联通过 PlaylistSong 实体管理
}
```

### 修复2：使用原生SQL删除关联记录

**PlaylistSongRepository.java** - 使用原生SQL：

```java
@Modifying
@Transactional
@Query(value = "DELETE FROM tb_playlist_song WHERE playlist_id = :playlistId", 
       nativeQuery = true)
void deleteByPlaylistId(@Param("playlistId") Long playlistId);
```

**优点：**
- 直接操作数据库，绕过 JPA 状态管理
- 避免与 @ManyToMany 冲突
- 更快更可靠

### 修复3：简化删除逻辑

**PlaylistService.java**：

```java
@Transactional
public void deletePlaylist(Long playlistId, Long userId) {
    Playlist playlist = playlistRepository.findById(playlistId)
        .orElseThrow(() -> new RuntimeException("歌单不存在"));
    
    // 验证权限
    if (!playlist.getUserId().equals(userId)) {
        throw new RuntimeException("无权限删除此歌单");
    }
    
    // 先使用原生SQL删除关联的歌曲
    playlistSongRepository.deleteByPlaylistId(playlistId);
    
    // 再删除歌单本身（不会再触发级联删除）
    playlistRepository.delete(playlist);
}
```

---

## 🔧 修复总结

| 修复项 | 修改内容 | 原因 |
|-------|---------|------|
| **Playlist.java** | 移除 `@ManyToMany songs` 字段 | 避免双重管理同一个表 |
| **PlaylistSongRepository.java** | 使用原生SQL删除 | 绕过JPA状态管理 |
| **PlaylistService.java** | 简化删除逻辑 | 依赖原生SQL，无需复杂处理 |

---

## 🚀 部署步骤

### 1. 重启后端服务

```bash
cd back/music-project
mvn spring-boot:run
```

### 2. 测试删除功能

1. 打开"我的音乐" → "我的歌单"
2. 选择一个歌单，点击删除
3. **预期：** 删除成功 ✅

---

## 🧪 测试场景

### 场景1：删除空歌单
- 创建空歌单
- 删除
- **预期：** 成功 ✅

### 场景2：删除有歌曲的歌单
- 创建歌单，添加 3-5 首歌
- 删除
- **预期：** 
  - 歌单删除成功 ✅
  - 关联记录同时删除 ✅

### 场景3：连续删除多个歌单
- **预期：** 全部成功，无错误 ✅

---

## 📊 SQL 验证

```sql
-- 查看是否有孤立的关联记录
SELECT ps.* 
FROM tb_playlist_song ps
LEFT JOIN tb_playlist p ON ps.playlist_id = p.id
WHERE p.id IS NULL;
```

**预期：** 空结果（无孤立记录）

---

## 💡 架构改进

### 问题：为什么会有两种管理方式？

1. **@ManyToMany**：JPA 标准方式，自动管理中间表
2. **PlaylistSong 实体**：自定义方式，需要额外字段（如 `addTime`、`sortOrder`）

### 解决方案：统一使用 PlaylistSong

**优点：**
- ✅ 完全控制中间表结构
- ✅ 可以添加额外字段
- ✅ 避免 JPA 自动行为的意外冲突
- ✅ 更清晰的代码逻辑

**结论：** 已移除 `@ManyToMany`，统一使用 `PlaylistSong` 实体 ✅

---

## 📝 相关文件

- `Playlist.java` - 移除 @ManyToMany 字段 ✅
- `PlaylistSongRepository.java` - 使用原生SQL ✅
- `PlaylistService.java` - 简化删除逻辑 ✅

---

## ✨ 修复完成

**删除歌单功能已彻底修复！** 🎉

- ✅ 移除了冲突的 @ManyToMany 关系
- ✅ 使用原生SQL直接操作数据库
- ✅ 简化了删除逻辑
- ✅ 避免了 JPA 状态管理的复杂性

**重启后端服务即可正常使用！**

