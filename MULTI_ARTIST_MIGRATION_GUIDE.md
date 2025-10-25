# 多歌手功能迁移指南

## 📋 功能概述

将音乐系统从"一首歌对应一个歌手"升级为"一首歌可以有多个歌手"（合唱功能）。

## 🔧 数据库变更

### 1. 新增中间表

JPA 会自动创建以下表：

```sql
CREATE TABLE song_artist (
    song_id BIGINT NOT NULL,
    artist_id BIGINT NOT NULL,
    PRIMARY KEY (song_id, artist_id),
    FOREIGN KEY (song_id) REFERENCES tb_song(id) ON DELETE CASCADE,
    FOREIGN KEY (artist_id) REFERENCES tb_artist(id) ON DELETE CASCADE
);
```

### 2. 旧数据迁移

如果数据库中已经有歌曲数据，需要将现有的 `artistId` 迁移到新的 `song_artist` 表中：

```sql
-- 1. 将旧的 artistId 关系迁移到 song_artist 表
INSERT INTO song_artist (song_id, artist_id)
SELECT id, artistId 
FROM tb_song 
WHERE artistId IS NOT NULL;

-- 2. (可选) 删除旧字段
-- 注意：只有在确认迁移成功后才执行！
-- ALTER TABLE tb_song DROP COLUMN artistId;
-- ALTER TABLE tb_song DROP COLUMN artistName;
```

⚠️ **注意**: 不要立即删除 `artistId` 和 `artistName` 字段！等确认新功能运行正常后再删除。

## 📦 后端变更

### 1. 实体类 (Song.java)

**修改前:**
```java
@Column(nullable = false)
private Long artistId;

@Column(length = 100)
private String artistName;
```

**修改后:**
```java
@ManyToMany(fetch = FetchType.EAGER)
@JoinTable(
    name = "song_artist",
    joinColumns = @JoinColumn(name = "song_id"),
    inverseJoinColumns = @JoinColumn(name = "artist_id")
)
private List<Artist> artists = new ArrayList<>();
```

### 2. DTO (SongRequest.java)

**修改前:**
```java
@NotNull(message = "歌手ID不能为空")
private Long artistId;
private String artistName;
```

**修改后:**
```java
@NotEmpty(message = "至少需要选择一位歌手")
private List<Long> artistIds;
```

### 3. Repository (SongRepository.java)

**修改前:**
```java
List<Song> findByArtistId(Long artistId);

@Query("SELECT s FROM Song s WHERE s.title LIKE %:keyword% OR s.artistName LIKE %:keyword%")
Page<Song> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
```

**修改后:**
```java
@Query("SELECT DISTINCT s FROM Song s JOIN s.artists a WHERE a.id = :artistId")
List<Song> findByArtistId(@Param("artistId") Long artistId);

@Query("SELECT DISTINCT s FROM Song s LEFT JOIN s.artists a WHERE s.title LIKE %:keyword% OR a.name LIKE %:keyword%")
Page<Song> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
```

### 4. Service (SongService.java)

新增了对多歌手的处理逻辑：
```java
@Transactional
public Song createSong(SongRequest request) {
    Song song = new Song();
    BeanUtils.copyProperties(request, song, "artistIds");
    
    // 关联歌手
    if (request.getArtistIds() != null && !request.getArtistIds().isEmpty()) {
        List<Artist> artists = new ArrayList<>();
        for (Long artistId : request.getArtistIds()) {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("歌手ID " + artistId + " 不存在"));
            artists.add(artist);
        }
        song.setArtists(artists);
    }
    
    return songRepository.save(song);
}
```

## 🎨 前端变更

### 1. 管理后台 (SongManagement.vue)

**歌手选择改为多选：**
```vue
<!-- 修改前 -->
<el-select v-model="formData.artistId" placeholder="请选择歌手">
  <el-option v-for="artist in artists" :key="artist.id" :label="artist.name" :value="artist.id" />
</el-select>

<!-- 修改后 -->
<el-select 
  v-model="formData.artistIds" 
  placeholder="请选择歌手（可多选）" 
  multiple 
  collapse-tags 
  collapse-tags-tooltip
>
  <el-option v-for="artist in artists" :key="artist.id" :label="artist.name" :value="artist.id" />
</el-select>
```

**表格显示多个歌手：**
```vue
<!-- 修改前 -->
<el-table-column prop="artistName" label="歌手" width="120" />

<!-- 修改后 -->
<el-table-column label="歌手" width="150">
  <template #default="{ row }">
    {{ row.artists && row.artists.length > 0 ? row.artists.map(a => a.name).join(' / ') : '-' }}
  </template>
</el-table-column>
```

### 2. 用户页面 (Home.vue, Search.vue)

**显示多个歌手名称，支持点击跳转：**
```vue
<!-- 修改前 -->
<div class="song-artist clickable" @click.stop="goToArtist(song.artistId)">
  {{ song.artistName }}
</div>

<!-- 修改后 -->
<div class="song-artist">
  <template v-for="(artist, idx) in song.artists || []" :key="artist.id">
    <span class="clickable" @click.stop="goToArtist(artist.id)">{{ artist.name }}</span>
    <span v-if="idx < (song.artists?.length || 0) - 1"> / </span>
  </template>
  <span v-if="!song.artists || song.artists.length === 0">未知歌手</span>
</div>
```

## 🚀 部署步骤

### 1. 备份数据库
```bash
mysqldump -u root -p sheep_music > sheep_music_backup_$(date +%Y%m%d).sql
```

### 2. 更新后端代码
```bash
cd back/music-project
git pull
mvn clean install
```

### 3. 启动后端（JPA 自动创建中间表）
```bash
mvn spring-boot:run
```

### 4. 执行数据迁移脚本
```sql
-- 连接到数据库
mysql -u root -p sheep_music

-- 执行迁移
INSERT INTO song_artist (song_id, artist_id)
SELECT id, artistId 
FROM tb_song 
WHERE artistId IS NOT NULL;

-- 验证
SELECT s.title, GROUP_CONCAT(a.name SEPARATOR ', ') as artists
FROM tb_song s
LEFT JOIN song_artist sa ON s.id = sa.song_id
LEFT JOIN tb_artist a ON sa.artist_id = a.id
GROUP BY s.id
LIMIT 10;
```

### 5. 更新前端代码
```bash
cd front/sheep-music
git pull
npm install
npm run serve
```

### 6. 测试功能
- ✅ 新增歌曲（选择多个歌手）
- ✅ 编辑歌曲（修改歌手）
- ✅ 显示歌曲列表（查看多个歌手）
- ✅ 搜索功能（按任一歌手搜索）
- ✅ 歌手详情页（查看该歌手的所有歌曲）

## 📝 API 变更

### 创建/更新歌曲

**请求体变更：**
```json
// 修改前
{
  "title": "告白气球",
  "artistId": 1,
  "artistName": "周杰伦",
  "url": "https://...",
  ...
}

// 修改后
{
  "title": "告白气球",
  "artistIds": [1, 2],  // 支持多个歌手
  "url": "https://...",
  ...
}
```

### 返回数据变更

```json
// 修改前
{
  "id": 1,
  "title": "告白气球",
  "artistId": 1,
  "artistName": "周杰伦",
  ...
}

// 修改后
{
  "id": 1,
  "title": "告白气球",
  "artists": [
    { "id": 1, "name": "周杰伦", "avatar": "..." },
    { "id": 2, "name": "方文山", "avatar": "..." }
  ],
  ...
}
```

## ⚠️ 注意事项

1. **向后兼容**: 如果有其他系统依赖旧的 API 格式，需要保留兼容层
2. **性能优化**: 使用了 `FetchType.EAGER` 来避免 N+1 查询问题
3. **数据完整性**: 添加了外键约束，删除歌曲或歌手时会自动清理关联数据
4. **搜索优化**: 使用 `DISTINCT` 避免多歌手导致的重复结果

## 🎉 新功能示例

现在可以创建合唱歌曲了！

**例如：**
- "夜曲" - 周杰伦 / 袁咏琳
- "说好不哭" - 周杰伦 / 五月天阿信
- "等你下课" - 周杰伦 / 杨瑞代

## 🐛 问题排查

### Q1: 启动后报错 "artistId field not found"
**A**: 确保 Song 实体类已经移除了 `artistId` 字段，并添加了 `artists` 字段。

### Q2: 前端显示 "未知歌手"
**A**: 检查后端是否正确返回了 `artists` 数组。确认 `FetchType.EAGER` 已设置。

### Q3: 搜索功能无法按歌手搜索
**A**: 确认 `SongRepository.searchByKeyword` 方法已更新为 JOIN `artists` 表。

### Q4: 旧数据没有歌手信息
**A**: 执行数据迁移脚本，将 `artistId` 迁移到 `song_artist` 表。

---

**更新时间**: 2025-10-19
**版本**: v2.0 - 多歌手支持





