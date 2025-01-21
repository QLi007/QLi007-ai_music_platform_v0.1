# AI音乐平台微服务设计文档 (详细版)

## 一、系统架构

### 1. 整体架构
```
                                    [客户端]
                                       ↓
                                    [Nginx]
                                       ↓
                                   [API网关]
                                       ↓
                                [服务注册中心]
                                       ↓
    +---------------+---------------+---------------+---------------+---------------+
    |               |               |               |               |               |
[用户服务8081]  [音乐服务8082]  [AI服务8083]  [文件服务8084]  [播放列表服务8085]
    |               |               |               |               |               |
  [MySQL]        [MongoDB]        [Redis]         [MinIO]       [MongoDB]
```

### 2. 核心服务

#### 2.1 用户服务 (user-service)
```yaml
职责: 用户账号与认证管理
技术栈: 
  - Node.js/Express
  - MySQL (用户数据)
  - Redis (会话缓存)
  - JWT (身份认证)

API详情:
  POST /api/users/register:
    描述: 用户注册
    请求体:
      username: string     # 用户名(5-20字符)
      email: string       # 邮箱
      password: string    # 密码(8-20字符)
    响应:
      id: string          # 用户ID
      username: string    # 用户名
      token: string       # JWT令牌

  POST /api/users/login:
    描述: 用户登录
    请求体:
      email: string       # 邮箱
      password: string    # 密码
    响应:
      token: string       # JWT令牌
      refreshToken: string # 刷新令牌

  GET /api/users/profile:
    描述: 获取用户信息
    请求头:
      Authorization: Bearer <token>
    响应:
      id: string          # 用户ID
      username: string    # 用户名
      email: string       # 邮箱
      createdAt: date     # 创建时间
      preferences: object # 用户偏好设置

  PUT /api/users/profile:
    描述: 更新用户信息
    请求头:
      Authorization: Bearer <token>
    请求体:
      username?: string   # 可选，新用户名
      password?: string   # 可选，新密码
      preferences?: object # 可选，偏好设置
    响应:
      success: boolean    # 更新结果

错误处理:
  - 400: 请求参数错误
  - 401: 未授权访问
  - 409: 用户名/邮箱已存在
  - 500: 服务器内部错误

数据验证:
  username:
    - 长度: 5-20字符
    - 格式: 字母、数字、下划线
    - 唯一性检查
  
  email:
    - 格式: 有效邮箱格式
    - 唯一性检查
    
  password:
    - 长度: 8-20字符
    - 复杂度: 包含字母和数字
```

#### 2.2 音乐服务 (music-service)
```yaml
职责: 音乐元数据与状态管理
技术栈:
  - Node.js/Express
  - MongoDB (音乐数据)
  - Redis (缓存热门数据)
  - Elasticsearch (音乐搜索)

API详情:
  GET /api/music:
    描述: 获取音乐列表
    参数:
      page: number       # 页码，默认1
      limit: number      # 每页数量，默认20
      sort: string       # 排序方式(latest|popular)
      visibility: string # 可见性筛选(public|private|all)
    响应:
      items: array       # 音乐列表
      total: number      # 总数量
      page: number       # 当前页码

  GET /api/music/{id}:
    描述: 获取音乐详情
    参数:
      id: string         # 音乐ID
    响应:
      id: string         # 音乐ID
      name: string       # 音乐名称
      userId: string     # 创建者ID
      fileId: string     # 文件ID
      visibility: string # 可见性
      stats: object      # 统计信息
      createdAt: date    # 创建时间

  PUT /api/music/{id}:
    描述: 更新音乐信息
    请求头:
      Authorization: Bearer <token>
    请求体:
      name?: string      # 音乐名称
      visibility?: string # 可见性
    响应:
      success: boolean   # 更新结果

  GET /api/music/search:
    描述: 搜索音乐
    参数:
      q: string          # 搜索关键词
      type: string       # 搜索类型(name|tag|style)
      page: number       # 页码
      limit: number      # 每页数量
    响应:
      items: array       # 搜索结果
      total: number      # 总数量

数据模型:
  音乐文档:
    _id: ObjectId        # 音乐ID
    name: string         # 音乐名称
    userId: string       # 创建者ID
    fileId: string       # 文件ID
    visibility: string   # 可见性(public|private)
    tags: string[]       # 标签列表
    style: string        # 音乐风格
    duration: number     # 时长(秒)
    stats: {
      plays: number      # 播放次数
      likes: number      # 喜欢数量
      shares: number     # 分享次数
    }
    metadata: {
      format: string     # 文件格式
      bitrate: number    # 比特率
      size: number       # 文件大小
    }
    createdAt: date      # 创建时间
    updatedAt: date      # 更新时间

索引设计:
  - name_text_tags_text: 文本搜索索引
  - userId_1: 用户音乐查询
  - visibility_1: 可见性筛选
  - createdAt_-1: 时间排序
```

#### 2.3 AI服务 (ai-service)
```yaml
职责: AI音乐生成任务管理
技术栈:
  - Node.js/Express
  - Redis (任务队列)
  - MongoDB (任务记录)
  - Bull (队列管理)

支持模型:
  - AudioCraft:
      类型: 音频生成
      特点: 高质量、多风格
      限制: 需要GPU
      
  - Riffusion:
      类型: 音乐生成
      特点: 实时生成、轻量级
      限制: 短音频片段
      
  - MusicGen:
      类型: 音乐生成
      特点: 文本引导
      限制: 固定长度

API详情:
  GET /api/ai/models:
    描述: 获取可用AI模型
    响应:
      models: [
        {
          id: string       # 模型ID
          name: string     # 模型名称
          description: string # 描述
          features: object # 支持特性
          limits: object   # 使用限制
        }
      ]

  POST /api/ai/generate:
    描述: 创建生成任务
    请求体:
      modelId: string     # 模型ID
      prompt: string      # 生成提示
      params: {
        duration: number  # 时长(秒)
        style: string    # 音乐风格
        tempo: number    # 节奏
        instruments: string[] # 乐器
      }
    响应:
      taskId: string      # 任务ID
      status: string      # 任务状态

  GET /api/ai/tasks/{id}:
    描述: 获取任务状态
    响应:
      id: string          # 任务ID
      status: string      # 状态
      progress: number    # 进度(0-100)
      result?: {
        fileId: string    # 生成文件ID
        duration: number  # 实际时长
      }
      error?: string      # 错误信息

任务状态流转:
  pending → processing → completed|failed
  
错误处理:
  - MODEL_NOT_FOUND: 模型不存在
  - INVALID_PARAMS: 参数无效
  - GENERATION_FAILED: 生成失败
  - QUOTA_EXCEEDED: 超出配额

任务队列设计:
  - 优先级队列
  - 失败重试策略
  - 任务超时处理
  - 并发控制
```

#### 2.4 文件服务 (file-service)
```yaml
职责: 音频文件存储管理
技术栈:
  - Node.js/Express
  - MinIO (对象存储)
  - MongoDB (元数据)
  - Redis (缓存)

API详情:
  POST /api/files/upload:
    描述: 上传文件
    请求:
      类型: multipart/form-data
      参数:
        file: binary     # 文件数据
        type: string     # 文件类型
        metadata?: object # 元数据
    响应:
      fileId: string     # 文件ID
      url: string        # 访问URL
      metadata: object   # 文件信息

  GET /api/files/{id}:
    描述: 获取文件
    参数:
      id: string         # 文件ID
      download?: boolean # 是否下载
    响应:
      文件流或预签名URL

  DELETE /api/files/{id}:
    描述: 删除文件
    响应:
      success: boolean   # 删除结果

存储策略:
  - 文件分片上传
  - 文件格式验证
  - 病毒扫描
  - 存储生命周期
  
文件处理:
  - 音频格式转换
  - 音频压缩
  - 元数据提取
  - 缓存策略
```

#### 2.5 播放列表服务 (playlist-service)
```yaml
职责: 播放列表管理
技术栈:
  - Node.js/Express
  - MongoDB (列表数据)
  - Redis (热门列表缓存)

API详情:
  GET /api/playlists:
    描述: 获取用户播放列表
    参数:
      userId: string     # 用户ID
    响应:
      items: array       # 列表数组
      total: number      # 总数量

  POST /api/playlists:
    描述: 创建播放列表
    请求体:
      name: string       # 列表名称
      description?: string # 描述
    响应:
      id: string         # 列表ID
      name: string       # 列表名称

  PUT /api/playlists/{id}:
    描述: 更新播放列表
    请求体:
      name?: string      # 新名称
      description?: string # 新描述
    响应:
      success: boolean   # 更新结果

  POST /api/playlists/{id}/songs:
    描述: 添加歌曲到列表
    请求体:
      musicId: string    # 音乐ID
      position?: number  # 插入位置
    响应:
      success: boolean   # 添加结果

数据模型:
  播放列表文档:
    _id: ObjectId        # 列表ID
    userId: string       # 创建者ID
    name: string         # 列表名称
    description: string  # 描述
    songs: [{
      musicId: string    # 音乐ID
      addedAt: date      # 添加时间
      position: number   # 位置序号
    }]
    stats: {
      songCount: number  # 歌曲数量
      totalDuration: number # 总时长
      lastUpdated: date  # 最后更新
    }
    createdAt: date      # 创建时间
    updatedAt: date      # 更新时间

索引设计:
  - userId_1: 用户列表查询
  - songs.musicId_1: 音乐查询
  - updatedAt_-1: 更新时间排序
```

### 3. 服务通信

#### 3.1 事件通信
```yaml
使用Redis Pub/Sub:
  频道设计:
    music-created:        # 新音乐创建
    music-updated:        # 音乐更新
    generation-completed: # 生成完成
    playlist-updated:     # 列表更新

消息格式:
  {
    type: string         # 事件类型
    payload: object      # 事件数据
    timestamp: number    # 时间戳
    source: string       # 源服务
  }
```

#### 3.2 服务依赖
```yaml
用户服务:
  - Redis (缓存)
  - MySQL (数据)

音乐服务:
  - MongoDB (数据)
  - Redis (缓存)
  - 文件服务 (存储)
  - AI服务 (生成)

AI服务:
  - Redis (队列)
  - MongoDB (记录)
  - 文件服务 (存储)

文件服务:
  - MinIO (存储)
  - MongoDB (元数据)
  - Redis (缓存)

播放列表服务:
  - MongoDB (数据)
  - Redis (缓存)
  - 音乐服务 (元数据)
```

### 4. 开发环境

```yaml
# docker-compose.yml
version: '3.8'
services:
  mysql:
    image: mysql:8
    environment:
      MYSQL_ROOT_PASSWORD: ${MYSQL_ROOT_PASSWORD}
      MYSQL_DATABASE: ${MYSQL_DATABASE}
    volumes:
      - mysql_data:/var/lib/mysql
    ports: ["3306:3306"]
    
  mongodb:
    image: mongo:latest
    environment:
      MONGO_INITDB_ROOT_USERNAME: ${MONGO_ROOT_USER}
      MONGO_INITDB_ROOT_PASSWORD: ${MONGO_ROOT_PASSWORD}
    volumes:
      - mongo_data:/data/db
    ports: ["27017:27017"]
    
  redis:
    image: redis:latest
    volumes:
      - redis_data:/data
    ports: ["6379:6379"]
    
  minio:
    image: minio/minio
    environment:
      MINIO_ROOT_USER: ${MINIO_ROOT_USER}
      MINIO_ROOT_PASSWORD: ${MINIO_ROOT_PASSWORD}
    volumes:
      - minio_data:/data
    ports: 
      - "9000:9000"
      - "9001:9001"
    command: server /data --console-address ":9001"

volumes:
  mysql_data:
  mongo_data:
  redis_data:
  minio_data:
```

### 5. 测试规范

```yaml
单元测试:
  范围:
    - 业务逻辑
    - 数据模型
    - 工具函数
    - 中间件
  
  命名规范:
    - *.test.ts: 单元测试
    - *.spec.ts: 集成测试
    
  目录结构:
    src/
      └── modules/
          └── music/
              ├── music.service.ts
              ├── music.controller.ts
              └── __tests__/
                  ├── music.service.test.ts
                  └── music.controller.test.ts

集成测试:
  范围:
    - API接口测试
    - 数据库操作
    - 服务通信
    - 中间件链
    
  环境准备:
    - 测试数据库
    - Mock外部服务
    - 环境变量

性能测试:
  工具: k6
  指标:
    - 响应时间
    - 并发用户数
    - 错误率
    - 资源使用
```

### 6. 部署流程

```yaml
环境要求:
  - Node.js 18+
  - Docker 20+
  - Docker Compose 2+
  - 4GB+ RAM
  - 2+ CPU Cores

部署步骤:
  1. 环境准备:
     - 安装依赖
     - 配置环境变量
     - 准备存储卷
     
  2. 数据库部署:
     - 启动MySQL
     - 启动MongoDB
     - 初始化数据
     
  3. 中间件部署:
     - 启动Redis
     - 启动MinIO
     - 配置存储桶
     
  4. 应用部署:
     - 构建镜像
     - 启动服务
     - 健康检查
     
  5. 网关配置:
     - 配置路由
     - 设置SSL
     - 启动Nginx

监控方案:
  基础监控:
    - CPU使用率
    - 内存使用率
    - 磁盘使用率
    - 网络流量
    
  应用监控:
    - API响应时间
    - 错误率统计
    - 并发连接数
    - 队列长度
    
  业务监控:
    - 用户活跃度
    - 音乐生成量
    - 存储使用量
    - 转化率
```

### 7. 错误处理

```yaml
错误码设计:
  系统错误: 5xxx
    - 5000: 内部错误
    - 5001: 服务不可用
    - 5002: 数据库错误
    
  业务错误: 4xxx
    - 4000: 参数错误
    - 4001: 未授权
    - 4002: 禁止访问
    - 4003: 资源不存在
    - 4004: 配额超限
    
  第三方错误: 3xxx
    - 3000: AI服务错误
    - 3001: 存储服务错误
    - 3002: 外部API错误

错误响应格式:
  {
    code: number        # 错误码
    message: string     # 错误信息
    details?: object    # 详细信息
    requestId: string   # 请求ID
    timestamp: number   # 时间戳
  }
```

### 8. 安全规范

```yaml
认证机制:
  - JWT令牌
  - 刷新令牌
  - 会话管理
  
授权控制:
  - 基于角色
  - 资源级别
  - 操作级别
  
数据安全:
  - 传输加密(HTTPS)
  - 密码加密(bcrypt)
  - 敏感信息脱敏
  
访问控制:
  - 频率限制
  - IP白名单
  - 并发控制
```
