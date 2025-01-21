# AI音乐平台

基于人工智能的音乐创作与分享平台。

## 项目概述

AI音乐平台是一个创新的音乐创作平台，结合了AI技术、社交功能和区块链激励机制。平台支持用户使用AI生成音乐，分享创作，获得通证激励，并将音乐作品铸造为NFT进行交易。

## 技术栈

### 后端
- Spring Boot 3.2
- Spring Security
- Spring Data JPA
- PostgreSQL
- Redis
- Kafka
- Docker

### 前端
- Next.js 14
- React 18
- TypeScript
- TailwindCSS
- Shadcn UI

### 区块链
- Ethereum/Polygon
- Solidity
- Web3.js/Ethers.js
- IPFS

## 版本规划

- V0.1: 基础音乐生成
- V0.2: 用户认证系统
- V0.3: 社交功能
- V0.4: 推荐系统
- V0.5: 区块链激励

## 开发环境搭建

1. 克隆仓库
```bash
git clone [repository-url]
cd ai-music-platform
```

2. 安装依赖
```bash
# 后端
cd backend
mvn install

# 前端
cd frontend
npm install
```

3. 环境配置
- 复制 `.env.example` 为 `.env`
- 配置必要的环境变量

4. 启动服务
```bash
# 使用Docker Compose启动所有服务
docker-compose up -d
```

## 项目文档

- [基础音乐生成](./V0.1_BASIC_MUSIC_GENERATION.md)
- [用户认证系统](./V0.2_USER_AUTH.md)
- [社交功能](./V0.3_SOCIAL_FEATURES.md)
- [推荐系统](./V0.4_RECOMMENDATION.md)
- [区块链激励](./V0.5_BLOCKCHAIN.md)

## 开发规范

- 遵循测试驱动开发(TDD)
- 代码提交前必须通过单元测试
- 遵循代码规范和最佳实践
- 及时更新文档

## 贡献指南

1. Fork 项目
2. 创建特性分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 开启 Pull Request

## 许可证

[MIT License](LICENSE) 