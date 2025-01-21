# AI音乐平台

基于人工智能的音乐创作和分享平台。

## 功能特性

- AI音乐生成
- 音乐播放器
- 用户系统
- 社交分享
- 推荐系统
- 区块链激励

## 技术栈

### 前端
- Next.js 14
- React 18
- TypeScript 5
- Tailwind CSS
- Shadcn UI

### 后端
- Node.js 18
- Express
- TypeScript
- PostgreSQL
- Redis

### DevOps
- Docker
- Docker Compose
- GitHub Actions

## 快速开始

1. 克隆项目
```bash
git clone <repository-url>
cd ai-music-platform
```

2. 安装依赖
```bash
npm install
```

3. 配置环境变量
```bash
cp .env.example .env
# 编辑.env文件配置必要的环境变量
```

4. 启动开发环境
```bash
npm run dev
```

5. 访问应用
- 前端: http://localhost:3000
- 后端API: http://localhost:4000

## 项目结构

```
ai-music-platform/
├── frontend/           # 前端应用
├── backend/           # 后端API服务
├── services/          # 微服务
│   └── music-generation-service/
├── config/           # 配置文件
├── docs/             # 文档
└── tests/            # 测试
```

## 开发指南

### 代码规范
- 使用ESLint进行代码检查
- 使用Prettier进行代码格式化
- 遵循TypeScript严格模式

### 提交规范
- feat: 新功能
- fix: 修复
- docs: 文档更新
- style: 代码格式
- refactor: 重构
- test: 测试
- chore: 构建过程或辅助工具的变动

### 分支管理
- main: 主分支
- develop: 开发分支
- feature/*: 功能分支
- bugfix/*: 修复分支

## 测试

```bash
# 运行所有测试
npm test

# 运行特定工作区测试
npm test -w <workspace-name>
```

## 部署

### 测试环境
```bash
docker-compose -f docker-compose.yml -f docker-compose.test.yml up -d
```

### 生产环境
```bash
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

## 贡献指南

1. Fork 项目
2. 创建功能分支
3. 提交变更
4. 推送到分支
5. 创建Pull Request

## 版本历史

- v0.1.0 - 基础音乐生成
- v0.2.0 - 用户认证系统
- v0.3.0 - 社交功能
- v0.4.0 - 推荐系统
- v0.5.0 - 区块链激励

## 许可证

[MIT](LICENSE) 