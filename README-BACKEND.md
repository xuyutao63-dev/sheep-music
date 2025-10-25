# Sheep Music 后端项目

## 📋 技术栈

- Spring Boot 2.4.13
- Spring Data JPA (Hibernate)
- Spring Security + JWT
- MySQL 8.0
- Knife4j (Swagger)
- Lombok

## 🚀 启动步骤

### 1. 创建数据库

在 MySQL 中执行以下命令创建数据库：

```sql
CREATE DATABASE IF NOT EXISTS sheep_music DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

或者直接在 Navicat 中：
- 右键 → 新建数据库
- 数据库名：`sheep_music`
- 字符集：`utf8mb4`
- 排序规则：`utf8mb4_unicode_ci`

### 2. 修改数据库配置

打开 `src/main/resources/application.yml`，修改数据库连接信息：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/sheep_music?useUnicode=true&characterEncoding=utf-8&useSSL=false&serverTimezone=Asia/Shanghai
    username: root        # 修改为你的MySQL用户名
    password: root        # 修改为你的MySQL密码
```

### 3. 启动项目

方式一：使用 IDE（推荐）
- 打开 `SheepMusicApplication.java`
- 点击运行按钮

方式二：使用 Maven
```bash
mvn clean install
mvn spring-boot:run
```

### 4. 访问接口文档

启动成功后，访问：
- Knife4j文档：http://localhost:9000/doc.html

## 📚 API 接口说明

### 用户认证接口

#### 1. 用户注册
- **URL**：`POST /auth/register`
- **请求体**：
```json
{
  "username": "testuser",
  "password": "123456",
  "nickname": "测试用户",
  "email": "test@example.com"
}
```
- **响应**：
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "testuser",
    "nickname": "测试用户",
    ...
  }
}
```

#### 2. 用户登录
- **URL**：`POST /auth/login`
- **请求体**：
```json
{
  "username": "testuser",
  "password": "123456"
}
```
- **响应**：
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "token": "eyJhbGciOiJIUzUxMiJ9...",
    "userInfo": {
      "id": 1,
      "username": "testuser",
      "nickname": "测试用户",
      ...
    }
  }
}
```

#### 3. 获取用户信息（需要登录）
- **URL**：`GET /user/info`
- **请求头**：`Authorization: Bearer {token}`
- **响应**：用户信息

## 🔐 JWT Token 使用说明

登录成功后会返回 token，后续请求需要在请求头中携带：

```
Authorization: Bearer eyJhbGciOiJIUzUxMiJ9...
```

在 Swagger 文档中测试时：
1. 点击右上角"授权"按钮
2. 输入：`Bearer {你的token}`
3. 点击"授权"

## 📂 项目结构

```
src/main/java/com/example/sheepmusic/
├── config/              # 配置类
│   ├── SecurityConfig   # Spring Security配置
│   └── SwaggerConfig    # Swagger文档配置
├── controller/          # 控制器
│   ├── AuthController   # 认证控制器（登录注册）
│   └── UserController   # 用户控制器
├── dto/                 # 数据传输对象
│   ├── LoginRequest     # 登录请求
│   └── RegisterRequest  # 注册请求
├── entity/              # 实体类
│   └── User            # 用户实体
├── repository/          # 数据访问层
│   └── UserRepository  # 用户Repository
├── service/             # 业务逻辑层
│   └── UserService     # 用户服务
├── security/            # 安全相关
│   └── JwtAuthenticationFilter  # JWT过滤器
├── utils/               # 工具类
│   └── JwtUtil         # JWT工具类
├── common/              # 公共类
│   └── Result          # 统一返回结果
└── SheepMusicApplication.java  # 启动类
```

## ⚠️ 常见问题

1. **启动报错：数据库连接失败**
   - 检查 MySQL 是否启动
   - 检查数据库名、用户名、密码是否正确

2. **JWT Token 验证失败**
   - 确保 token 前面有 "Bearer " 前缀
   - 检查 token 是否过期（默认7天）

3. **跨域问题**
   - Controller 已添加 `@CrossOrigin` 注解
   - 如有问题，检查前端请求配置

## 📝 下一步开发

- [ ] 歌曲模块（Song）
- [ ] 歌手模块（Artist）
- [ ] 歌单模块（Playlist）
- [ ] 评论模块（Comment）
- [ ] 收藏模块（Favorite）


