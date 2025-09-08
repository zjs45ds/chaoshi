# 潮石音乐 - 接口文档

## 项目概述
本接口文档描述了潮石音乐应用的前后端API设计。前端基于Vue 3 + Vite构建，后端基于Spring Boot。项目包括用户、专辑、歌手、歌曲、播放列表、搜索、MV等功能模块。

## 技术栈

### 前端技术栈
- **框架**: Vue 3.4.0 + Vite 6.3.5
- **路由**: Vue Router 4.2.0
- **UI库**: Element Plus 2.10.4
- **HTTP客户端**: Axios 1.6.0
- **类型支持**: TypeScript 5.2.2
- **样式**: Sass + CSS Variables
- **动画**: @tsparticles/vue3
- **构建工具**: Vite + Rollup

### 后端技术栈
- **框架**: Spring Boot 2.7.x
- **ORM**: MyBatis Plus 3.5.x
- **数据库**: MySQL 8.0
- **对象存储**: MinIO 8.5.x
- **认证**: Spring Security + JWT
- **API文档**: Swagger/OpenAPI
- **构建工具**: Maven

## 前端项目结构
```
前端/
├── src/
│   ├── api/               # API接口封装
│   │   ├── album.js       # 专辑相关接口
│   │   ├── artist.js      # 歌手相关接口
│   │   ├── playlist.js    # 歌单相关接口
│   │   ├── search.js      # 搜索相关接口
│   │   ├── song.js        # 歌曲相关接口
│   │   ├── user.js        # 用户相关接口
│   │   └── mv.js          # MV相关接口
│   ├── components/        # 可复用组件
│   ├── views/             # 页面组件
│   ├── utils/             # 工具函数
│   │   ├── httpUtils.js   # HTTP请求工具
│   │   ├── errorHandler.js # 错误处理
│   │   ├── cacheManager.js # 缓存管理
│   │   ├── security.js    # 安全工具
│   │   └── imageUtils.js  # 图片处理
│   ├── types/             # TypeScript类型定义
│   ├── constants/         # 常量定义
│   └── assets/            # 静态资源
├── vite.config.js         # Vite配置
├── tsconfig.json          # TypeScript配置
└── package.json           # 依赖管理
```

## 后端项目结构
```
com.music
├── config/          # 配置类
├── controller/      # 控制器
├── service/         # 服务层
│   └── impl/        # 服务实现
├── mapper/          # Mapper接口
├── entity/          # 实体类
├── dto/             # 数据传输对象
├── vo/              # 视图对象
├── util/            # 工具类
└── MusicApplication.java  # 启动类
```

## 数据库设计

### 用户表(users)
```sql
CREATE TABLE users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(50) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL,
  email VARCHAR(100) UNIQUE,
  phone VARCHAR(20) UNIQUE,
  avatar VARCHAR(255),
  nickname VARCHAR(50),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 歌手表(artists)
```sql
CREATE TABLE artists (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  avatar VARCHAR(255),
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);
```

### 专辑表(albums)
```sql
CREATE TABLE albums (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  cover VARCHAR(255),
  artist_id INT NOT NULL,
  release_date DATE,
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (artist_id) REFERENCES artists(id)
);
```

### 歌曲表(songs)
```sql
CREATE TABLE songs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  album_id INT,
  artist_id INT NOT NULL,
  duration INT NOT NULL, -- 单位: 秒
  file_path VARCHAR(255) NOT NULL,
  lyrics TEXT,
  cover VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (album_id) REFERENCES albums(id),
  FOREIGN KEY (artist_id) REFERENCES artists(id)
);
```

### 播放列表表(playlists)
```sql
CREATE TABLE playlists (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  user_id INT NOT NULL,
  cover VARCHAR(255),
  description TEXT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id)
);
```

### 播放列表歌曲关联表(playlist_songs)
```sql
CREATE TABLE playlist_songs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  playlist_id INT NOT NULL,
  song_id INT NOT NULL,
  sort_order INT NOT NULL DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (playlist_id) REFERENCES playlists(id),
  FOREIGN KEY (song_id) REFERENCES songs(id),
  UNIQUE KEY unique_playlist_song (playlist_id, song_id)
);
```

### MV表(mvs)
```sql
CREATE TABLE mvs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  artist_id INT NOT NULL,
  cover VARCHAR(255),
  video_path VARCHAR(255) NOT NULL,
  duration INT NOT NULL, -- 单位: 秒
  description TEXT,
  play_count BIGINT DEFAULT 0,
  like_count INT DEFAULT 0,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  FOREIGN KEY (artist_id) REFERENCES artists(id)
);
```

### 用户收藏歌曲表(user_favorite_songs)
```sql
CREATE TABLE user_favorite_songs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  song_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (song_id) REFERENCES songs(id),
  UNIQUE KEY unique_user_song (user_id, song_id)
);
```

### 用户收藏播放列表表(user_favorite_playlists)
```sql
CREATE TABLE user_favorite_playlists (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  playlist_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (playlist_id) REFERENCES playlists(id),
  UNIQUE KEY unique_user_playlist (user_id, playlist_id)
);
```

### 用户收藏MV表(user_favorite_mvs)
```sql
CREATE TABLE user_favorite_mvs (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  mv_id INT NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (mv_id) REFERENCES mvs(id),
  UNIQUE KEY unique_user_mv (user_id, mv_id)
);
```

### 歌手时间线表(artist_timeline)
```sql
CREATE TABLE artist_timeline (
  id INT PRIMARY KEY AUTO_INCREMENT,
  artist_id INT NOT NULL,
  year INT NOT NULL,
  event_type VARCHAR(50) NOT NULL, -- album, single, award, concert等
  title VARCHAR(200) NOT NULL,
  description TEXT,
  image_url VARCHAR(255),
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (artist_id) REFERENCES artists(id)
);
```

### 文件存储表(file_storage)
```sql
CREATE TABLE file_storage (
  id INT PRIMARY KEY AUTO_INCREMENT,
  original_name VARCHAR(255) NOT NULL,
  stored_name VARCHAR(255) NOT NULL,
  file_path VARCHAR(500) NOT NULL,
  file_size BIGINT NOT NULL,
  mime_type VARCHAR(100),
  file_type ENUM('image', 'audio', 'video', 'document') NOT NULL,
  bucket_name VARCHAR(100) NOT NULL,
  upload_user_id INT,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (upload_user_id) REFERENCES users(id)
);
```

## 示例数据

### 用户表示例数据
```sql
INSERT INTO users (username, password, email, phone, avatar, nickname) VALUES
('johndoe', 'hashed_password', 'john@example.com', '13800138000', 'https://example.com/avatar1.jpg', '约翰'),
('janedoe', 'hashed_password', 'jane@example.com', '13900139000', 'https://example.com/avatar2.jpg', '简');
```

### 歌手表示例数据
```sql
INSERT INTO artists (name, avatar, description) VALUES
('薛之谦', 'https://example.com/xuezhiqian.jpg', '中国内地男歌手、音乐制作人、演员'),
('周杰伦', 'https://example.com/jaychou.jpg', '中国台湾男歌手、音乐人、演员、导演');
```

### 专辑表示例数据
```sql
INSERT INTO albums (name, cover, artist_id, release_date, description) VALUES
('意外', 'https://example.com/album1.jpg', 1, '2013-11-11', '薛之谦2013年发行的专辑'),
('十一月的萧邦', 'https://example.com/album2.jpg', 2, '2005-11-01', '周杰伦2005年发行的专辑');
```

### 歌曲表示例数据
```sql
INSERT INTO songs (name, album_id, artist_id, duration, file_path, lyrics, cover) VALUES
('演员', 1, 1, 280, 'https://example.com/song1.mp3', '简单点说话的方式简单点...', 'https://example.com/song1_cover.jpg'),
('青花瓷', 2, 2, 300, 'https://example.com/song2.mp3', '素胚勾勒出青花笔锋浓转淡...', 'https://example.com/song2_cover.jpg');
```

### 播放列表表示例数据
```sql
INSERT INTO playlists (name, user_id, cover, description) VALUES
('我的收藏', 1, 'https://example.com/playlist1.jpg', '我收藏的 favorite 歌曲'),
('开车必听', 2, 'https://example.com/playlist2.jpg', '适合开车时听的歌曲');
```

### 播放列表歌曲关联表示例数据
```sql
INSERT INTO playlist_songs (playlist_id, song_id, sort_order) VALUES
(1, 1, 1),
(1, 2, 2),
(2, 2, 1);
```

### MV表示例数据
```sql
INSERT INTO mvs (name, artist_id, cover, video_path, duration, description, play_count, like_count) VALUES
('演员', 1, 'https://minio.music-app.com/covers/mv_yanyan_cover.jpg', 'https://minio.music-app.com/videos/mv_yanyan.mp4', 240, '薛之谦代表作《演员》MV', 89562341, 1205673),
('青花瓷', 2, 'https://minio.music-app.com/covers/mv_qinghuaci_cover.jpg', 'https://minio.music-app.com/videos/mv_qinghuaci.mp4', 220, '周杰伦经典作品《青花瓷》MV', 156743298, 2340981);
```

### 歌手时间线表示例数据
```sql
INSERT INTO artist_timeline (artist_id, year, event_type, title, description, image_url) VALUES
(1, 2006, 'album', '《薛之谦》首张专辑', '薛之谦发行个人首张同名专辑', 'https://minio.music-app.com/timeline/xzq_2006.jpg'),
(1, 2013, 'album', '《意外》专辑', '发行第二张个人专辑《意外》', 'https://minio.music-app.com/timeline/xzq_2013.jpg'),
(1, 2015, 'single', '《演员》单曲', '发行代表作《演员》，获得巨大成功', 'https://minio.music-app.com/timeline/xzq_2015.jpg'),
(2, 2000, 'album', '《Jay》出道专辑', '周杰伦发行首张专辑《Jay》', 'https://minio.music-app.com/timeline/jay_2000.jpg'),
(2, 2005, 'album', '《十一月的萧邦》', '发行第六张专辑《十一月的萧邦》', 'https://minio.music-app.com/timeline/jay_2005.jpg');
```

### 用户收藏示例数据
```sql
INSERT INTO user_favorite_songs (user_id, song_id) VALUES
(1, 1),
(1, 2),
(2, 1);

INSERT INTO user_favorite_playlists (user_id, playlist_id) VALUES
(1, 2),
(2, 1);

INSERT INTO user_favorite_mvs (user_id, mv_id) VALUES
(1, 1),
(2, 1),
(2, 2);
```







## API接口定义

### 基础URL
所有接口的基础URL为: `https://api.music-app.com/v1`

### 用户接口

#### 1. 用户注册
- **URL**: `/users/register`
- **方法**: `POST`
- **请求体**: 
```json
{
  "username": "string",
  "password": "string",
  "email": "string",
  "phone": "string",
  "nickname": "string"
}
```
- **响应**: 
```json
{
  "code": 200,
  "message": "注册成功",
  "data": {
    "id": 1,
    "username": "string",
    "nickname": "string",
    "avatar": "string",
    "created_at": "2023-07-17T10:00:00Z"
  }
}
```

#### 2. 用户登录
- **URL**: `/users/login`
- **方法**: `POST`
- **请求体**: 
```json
{
  "username": "string",
  "password": "string"
}
```
- **响应**: 
```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "id": 1,
    "username": "string",
    "nickname": "string",
    "avatar": "string",
    "token": "jwt_token_here"
  }
}
```

### 歌手接口

#### 1. 获取歌手列表
- **URL**: `/artists`
- **方法**: `GET`
- **参数**: 
  - `page`: 页码 (默认: 1)
  - `limit`: 每页数量 (默认: 20)
  - `keyword`: 搜索关键词
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "list": [
      {
        "id": 1,
        "name": "薛之谦",
        "avatar": "https://example.com/xuezhiqian.jpg",
        "description": "中国内地男歌手、音乐制作人、演员"
      },
      {
        "id": 2,
        "name": "周杰伦",
        "avatar": "https://example.com/jaychou.jpg",
        "description": "中国台湾男歌手、音乐人、演员、导演"
      }
    ]
  }
}
```

#### 2. 获取歌手详情
- **URL**: `/artists/{id}`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "name": "薛之谦",
    "avatar": "https://example.com/xuezhiqian.jpg",
    "description": "中国内地男歌手、音乐制作人、演员",
    "albums": [
      {
        "id": 1,
        "name": "意外",
        "cover": "https://example.com/album1.jpg",
        "release_date": "2013-11-11"
      }
    ],
    "hot_songs": [
      {
        "id": 1,
        "name": "演员",
        "duration": 280,
        "cover": "https://example.com/song1_cover.jpg"
      }
    ]
  }
}
```

### 专辑接口

#### 1. 获取专辑列表
- **URL**: `/albums`
- **方法**: `GET`
- **参数**: 
  - `page`: 页码 (默认: 1)
  - `limit`: 每页数量 (默认: 20)
  - `artist_id`: 歌手ID
  - `keyword`: 搜索关键词
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 50,
    "list": [
      {
        "id": 1,
        "name": "意外",
        "cover": "https://example.com/album1.jpg",
        "artist_id": 1,
        "artist_name": "薛之谦",
        "release_date": "2013-11-11"
      },
      {
        "id": 2,
        "name": "十一月的萧邦",
        "cover": "https://example.com/album2.jpg",
        "artist_id": 2,
        "artist_name": "周杰伦",
        "release_date": "2005-11-01"
      }
    ]
  }
}
```

#### 2. 获取专辑详情
- **URL**: `/albums/{id}`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "name": "意外",
    "cover": "https://example.com/album1.jpg",
    "artist_id": 1,
    "artist_name": "薛之谦",
    "release_date": "2013-11-11",
    "description": "薛之谦2013年发行的专辑",
    "songs": [
      {
        "id": 1,
        "name": "演员",
        "duration": 280,
        "file_path": "https://example.com/song1.mp3",
        "lyrics": "简单点说话的方式简单点..."
      }
    ]
  }
}
```

### 歌曲接口

#### 1. 获取歌曲列表
- **URL**: `/songs`
- **方法**: `GET`
- **参数**: 
  - `page`: 页码 (默认: 1)
  - `limit`: 每页数量 (默认: 20)
  - `album_id`: 专辑ID
  - `artist_id`: 歌手ID
  - `keyword`: 搜索关键词
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 200,
    "list": [
      {
        "id": 1,
        "name": "演员",
        "album_id": 1,
        "album_name": "意外",
        "artist_id": 1,
        "artist_name": "薛之谦",
        "duration": 280,
        "cover": "https://example.com/song1_cover.jpg"
      },
      {
        "id": 2,
        "name": "青花瓷",
        "album_id": 2,
        "album_name": "十一月的萧邦",
        "artist_id": 2,
        "artist_name": "周杰伦",
        "duration": 300,
        "cover": "https://example.com/song2_cover.jpg"
      }
    ]
  }
}
```

#### 2. 获取歌曲详情
- **URL**: `/songs/{id}`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "name": "演员",
    "album_id": 1,
    "album_name": "意外",
    "artist_id": 1,
    "artist_name": "薛之谦",
    "duration": 280,
    "file_path": "https://example.com/song1.mp3",
    "lyrics": "简单点说话的方式简单点...",
    "cover": "https://example.com/song1_cover.jpg"
  }
}
```

### 播放列表接口

#### 1. 创建播放列表
- **URL**: `/playlists`
- **方法**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
- **请求体**: 
```json
{
  "name": "我的播放列表",
  "cover": "https://example.com/playlist_cover.jpg",
  "description": "这是我的新播放列表"
}
```
- **响应**: 
```json
{
  "code": 200,
  "message": "创建成功",
  "data": {
    "id": 1,
    "name": "我的播放列表",
    "cover": "https://example.com/playlist_cover.jpg",
    "description": "这是我的新播放列表",
    "user_id": 1,
    "created_at": "2023-07-17T10:00:00Z"
  }
}
```

#### 2. 获取播放列表列表
- **URL**: `/playlists`
- **方法**: `GET`
- **请求头**: 
  - `Authorization: Bearer {token}` (可选，用于获取当前用户的播放列表)
- **参数**: 
  - `page`: 页码 (默认: 1)
  - `limit`: 每页数量 (默认: 20)
  - `user_id`: 用户ID (用于获取指定用户的播放列表)
  - `keyword`: 搜索关键词
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 10,
    "list": [
      {
        "id": 1,
        "name": "我的收藏",
        "cover": "https://example.com/playlist1.jpg",
        "user_id": 1,
        "user_name": "johndoe",
        "song_count": 2
      },
      {
        "id": 2,
        "name": "开车必听",
        "cover": "https://example.com/playlist2.jpg",
        "user_id": 2,
        "user_name": "janedoe",
        "song_count": 1
      }
    ]
  }
}
```

#### 3. 获取播放列表详情
- **URL**: `/playlists/{id}`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "name": "我的收藏",
    "cover": "https://example.com/playlist1.jpg",
    "description": "我收藏的 favorite 歌曲",
    "user_id": 1,
    "user_name": "johndoe",
    "created_at": "2023-07-17T10:00:00Z",
    "songs": [
      {
        "id": 1,
        "name": "演员",
        "album_id": 1,
        "album_name": "意外",
        "artist_id": 1,
        "artist_name": "薛之谦",
        "duration": 280,
        "cover": "https://example.com/song1_cover.jpg",
        "sort_order": 1
      },
      {
        "id": 2,
        "name": "青花瓷",
        "album_id": 2,
        "album_name": "十一月的萧邦",
        "artist_id": 2,
        "artist_name": "周杰伦",
        "duration": 300,
        "cover": "https://example.com/song2_cover.jpg",
        "sort_order": 2
      }
    ]
  }
}
```

#### 4. 添加歌曲到播放列表
- **URL**: `/playlists/{id}/songs`
- **方法**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
- **请求体**: 
```json
{
  "song_id": 1,
  "sort_order": 1
}
```
- **响应**: 
```json
{
  "code": 200,
  "message": "添加成功",
  "data": {
    "playlist_id": 1,
    "song_id": 1,
    "sort_order": 1
  }
}
```

#### 5. 从播放列表移除歌曲
- **URL**: `/playlists/{id}/songs/{song_id}`
- **方法**: `DELETE`
- **请求头**: 
  - `Authorization: Bearer {token}`
- **响应**: 
```json
{
  "code": 200,
  "message": "移除成功",
  "data": null
}
```

### MV接口

#### 1. 获取MV列表
- **URL**: `/mv/list`
- **方法**: `GET`
- **参数**: 
  - `page`: 页码 (默认: 0)
  - `size`: 每页数量 (默认: 10)
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 100,
    "content": [
      {
        "id": 1,
        "name": "演员",
        "artist_id": 1,
        "artist_name": "薛之谦",
        "cover": "https://minio.music-app.com/covers/mv_yanyan_cover.jpg",
        "duration": 240,
        "play_count": 89562341
      }
    ]
  }
}
```

#### 2. 获取MV详情
- **URL**: `/mv/{id}`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "id": 1,
    "name": "演员",
    "artist_name": "薛之谦",
    "cover": "https://minio.music-app.com/covers/mv_yanyan_cover.jpg",
    "video_path": "https://minio.music-app.com/videos/mv_yanyan.mp4",
    "duration": 240,
    "play_count": 89562341
  }
}
```

#### 3. 收藏MV
- **URL**: `/mv/favorite`
- **方法**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
- **参数**: 
  - `mvId`: MV ID
  - `action`: 操作 ('like' 或 'unlike')
- **响应**: 
```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "mv_id": 1,
    "is_favorited": true
  }
}
```

### 搜索接口

#### 1. 综合搜索
- **URL**: `/search`
- **方法**: `GET`
- **参数**: 
  - `keyword`: 搜索关键词
  - `type`: 搜索类型 (song, album, artist, playlist, mv)
  - `page`: 页码 (默认: 0)
  - `size`: 每页数量 (默认: 10)
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total": 50,
    "content": [
      {
        "id": 1,
        "name": "演员",
        "artist_name": "薛之谦",
        "type": "song",
        "cover": "https://minio.music-app.com/covers/song1_cover.jpg"
      }
    ]
  }
}
```

### 歌手特定接口

#### 1. 获取薛之谦专辑数据
- **URL**: `/artists/xuezhiqian/albums`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "name": "意外",
      "cover": "https://minio.music-app.com/covers/album_yiwai.jpg",
      "release_date": "2013-11-11"
    }
  ]
}
```

#### 2. 获取薛之谦时间线
- **URL**: `/artists/xuezhiqian/timeline`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "year": 2006,
      "event_type": "album",
      "title": "《薛之谦》首张专辑",
      "description": "薛之谦发行个人首张同名专辑"
    }
  ]
}
```

#### 3. 获取薛之谦热门歌曲
- **URL**: `/artists/xuezhiqian/hotsongs`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": [
    {
      "id": 1,
      "rank": 1,
      "title": "演员",
      "album": "绅士",
      "year": "2015",
      "plays": "12.8亿"
    }
  ]
}
```

#### 4. 获取实时数据统计
- **URL**: `/artists/xuezhiqian/stats`
- **方法**: `GET`
- **响应**: 
```json
{
  "code": 200,
  "message": "成功",
  "data": {
    "total_plays": "156.8亿",
    "total_fans": "2341万",
    "album_count": 12,
    "song_count": 156
  }
}
```

### 文件上传接口

#### 1. 上传头像
- **URL**: `/user/upload-avatar`
- **方法**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
  - `Content-Type: multipart/form-data`
- **请求体**: 
  - `avatar`: 头像文件 (File)
- **响应**: 
```json
{
  "code": 200,
  "message": "上传成功",
  "data": {
    "avatar_url": "https://minio.music-app.com/avatars/user_123_avatar.jpg"
  }
}
```

### 用户收藏接口

#### 1. 收藏歌曲
- **URL**: `/song/favorite`
- **方法**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
- **参数**: 
  - `songId`: 歌曲ID
  - `action`: 操作 ('like' 或 'unlike')
- **响应**: 
```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "song_id": 1,
    "is_favorited": true
  }
}
```

#### 2. 收藏播放列表
- **URL**: `/playlist/favorite`
- **方法**: `POST`
- **请求头**: 
  - `Authorization: Bearer {token}`
- **参数**: 
  - `playlistId`: 播放列表ID
  - `action`: 操作 ('like' 或 'unlike')
- **响应**: 
```json
{
  "code": 200,
  "message": "收藏成功",
  "data": {
    "playlist_id": 1,
    "is_favorited": true
  }
}
```

## MinIO 对象存储配置

### MinIO 配置信息
```yaml
# application.yml
minio:
  endpoint: http://localhost:9000
  access-key: zhang123
  secret-key: zhang123
  bucket-name: chaoshi
  buckets:
    avatars: music-avatars     # 用户头像
    songs: music-songs         # 歌曲文件
    covers: music-covers       # 封面图片
    videos: music-videos       # MV视频
    timeline: music-timeline   # 时间线图片
```

### 文件路径约定
- **用户头像**: `music-avatars/user_{userId}_avatar.{ext}`
- **歌曲文件**: `music-songs/song_{songId}.{ext}`
- **封面图片**: `music-covers/{type}_{id}_cover.{ext}`
- **MV视频**: `music-videos/mv_{mvId}.{ext}`
- **时间线图片**: `music-timeline/{artist}_{year}.{ext}`

### MinIO Bucket 初始化示例
```java
@Component
public class MinioInitializer {
    
    @Autowired
    private MinioClient minioClient;
    
    @PostConstruct
    public void initBuckets() {
        String[] buckets = {"music-avatars", "music-songs", "music-covers", "music-videos", "music-timeline"};
        
        for (String bucket : buckets) {
            try {
                if (!minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build())) {
                    minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
                    // 设置公共读取权限
                    String policy = generatePublicReadPolicy(bucket);
                    minioClient.setBucketPolicy(SetBucketPolicyArgs.builder().bucket(bucket).config(policy).build());
                }
            } catch (Exception e) {
                log.error("初始化MinIO bucket失败: {}", bucket, e);
            }
        }
    }
}
```

## 错误码定义

```java
public enum ErrorCode {
    SUCCESS(200, "成功"),
    BAD_REQUEST(400, "请求参数错误"),
    UNAUTHORIZED(401, "未授权"),
    FORBIDDEN(403, "禁止访问"),
    NOT_FOUND(404, "资源不存在"),
    INTERNAL_ERROR(500, "服务器内部错误"),
    
    // 用户相关
    USER_NOT_FOUND(1001, "用户不存在"),
    USERNAME_EXISTED(1002, "用户名已存在"),
    PASSWORD_ERROR(1003, "密码错误"),
    TOKEN_EXPIRED(1004, "Token已过期"),
    
    // 文件相关
    FILE_UPLOAD_FAILED(2001, "文件上传失败"),
    FILE_TYPE_NOT_SUPPORTED(2002, "不支持的文件类型"),
    FILE_SIZE_EXCEEDED(2003, "文件大小超出限制"),
    
    // 歌曲相关
    SONG_NOT_FOUND(3001, "歌曲不存在"),
    ALBUM_NOT_FOUND(3002, "专辑不存在"),
    ARTIST_NOT_FOUND(3003, "歌手不存在"),
    
    // 播放列表相关
    PLAYLIST_NOT_FOUND(4001, "播放列表不存在"),
    PLAYLIST_ACCESS_DENIED(4002, "无权访问该播放列表");
    
    private final int code;
    private final String message;
    
    ErrorCode(int code, String message) {
        this.code = code;
        this.message = message;
    }
    
    public int getCode() { return code; }
    public String getMessage() { return message; }
}
```

## 数据库更新说明

基于前端代码分析，本文档对原有数据库设计进行了以下扩展：

1. **新增表结构**：
   - `mvs`: MV表，存储MV信息
   - `user_favorite_songs`: 用户收藏歌曲表
   - `user_favorite_playlists`: 用户收藏播放列表表
   - `user_favorite_mvs`: 用户收藏MV表
   - `artist_timeline`: 歌手时间线表
   - `file_storage`: 文件存储表

2. **MinIO集成**：
   - 所有文件存储都使用MinIO对象存储
   - 支持多种文件类型：音频、视频、图片
   - 提供统一的文件上传和访问接口

3. **接口完善**：
   - 根据前端 API 调用补充缺失的接口
   - 增加收藏功能相关接口
   - 增加文件上传相关接口
   - 增加歌手特定数据接口

所有数据都从后端获取，前端不再使用mock数据。