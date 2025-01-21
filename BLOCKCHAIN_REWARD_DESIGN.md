# AI音乐平台区块链奖励系统设计

## 1. 奖励服务 (reward-service)

### 1.1 基础架构
```yaml
职责: 区块链代币奖励管理
技术栈:
  - Node.js/Express
  - PostgreSQL (奖励记录)
  - Redis (缓存)
  - Web3.js/ethers.js (区块链交互)
  - Solidity (智能合约)

区块链选型:
  主网: Polygon
  测试网: Mumbai
  原因:
    - 低gas费用
    - 高TPS
    - EVM兼容
    - 成熟生态
```

### 1.2 API接口
```yaml
POST /api/rewards/earn:
  描述: 记录用户听歌获得奖励
  请求体:
    userId: string     # 用户ID
    musicId: string   # 音乐ID
    duration: number  # 收听时长(秒)
    type: string     # 活动类型(listen/create)
  响应:
    success: boolean  # 是否成功
    amount: number   # 奖励数量
    balance: number  # 当前余额

GET /api/rewards/balance:
  描述: 查询用户代币余额
  参数:
    userId: string   # 用户ID
  响应:
    balance: number  # 代币余额
    pending: number  # 待结算数量
    total: number   # 总计获得数量

GET /api/rewards/history:
  描述: 获取奖励历史
  参数:
    userId: string   # 用户ID
    page: number    # 页码
    limit: number   # 每页数量
  响应:
    items: array    # 奖励记录
    total: number   # 总记录数

POST /api/rewards/withdraw:
  描述: 提现代币
  请求体:
    userId: string   # 用户ID
    amount: number  # 提现数量
    address: string # 钱包地址
  响应:
    success: boolean # 是否成功
    txHash: string  # 交易哈希
```

### 1.3 数据模型
```yaml
奖励记录:
  id: bigint        # 记录ID
  userId: string    # 用户ID
  activityType: string # 活动类型
  musicId: string   # 音乐ID
  amount: decimal   # 奖励数量
  status: string    # 状态(pending/confirmed/failed)
  txHash: string    # 交易哈希
  createdAt: timestamp # 创建时间
  updatedAt: timestamp # 更新时间
```

### 1.4 智能合约
```yaml
MusicToken:
  功能:
    - 代币铸造
    - 代币转账
    - 余额查询
    - 奖励分发
  接口:
    - mint(address to, uint256 amount)
    - transfer(address to, uint256 amount)
    - balanceOf(address account)
    - reward(address user, uint256 amount)

RewardPool:
  功能:
    - 奖励池管理
    - 创作者分成
    - 平台分成
  接口:
    - distribute(address[] users, uint256[] amounts)
    - withdraw(uint256 amount)
    - getBalance()
```

## 2. 奖励机制

### 2.1 听歌奖励
```yaml
基础规则:
  - 基础奖励: 0.01 token/分钟
  - 每日上限: 100 token
  - 要求: 完整播放超过60%

创作奖励:
  - 单次播放: 0.05 token
  - 播放里程碑:
    1000次: 100 token
    10000次: 1000 token
  - 季度结算

分配比例:
  - 用户: 70%
  - 创作者: 20%
  - 平台: 10%
```

### 2.2 反作弊机制
```yaml
播放验证:
  - 心跳检测
  - 进度验证
  - IP分析
  - 设备指纹
  
账户限制:
  - 每日上限
  - 设备数限制
  - 风险评分
  
提现规则:
  - 最低提现: 100 token
  - KYC认证
  - 冷却期: 7天
```

## 3. 监控系统

### 3.1 监控指标
```yaml
链上数据:
  - 代币流通量
  - 持币地址数
  - 交易频率
  - Gas消耗
  
用户行为:
  - 日活跃度
  - 留存率
  - 提现转化
  - 参与度
```

## 4. 部署流程

### 4.1 开发环境
```yaml
工具链:
  - Hardhat
  - OpenZeppelin
  - Solidity ^0.8.0
  - Ethers.js

测试流程:
  1. 单元测试:
     - 合约功能测试
     - 边界条件测试
     - Gas优化测试
     
  2. 集成测试:
     - 多合约交互
     - 状态一致性
     - 权限验证
     
  3. 安全审计:
     - 静态分析
     - 动态测试
     - 第三方审计
```

### 4.2 部署步骤
```yaml
1. 合约部署:
   - 编译合约
   - 验证源码
   - 初始化参数
   
2. 权限配置:
   - 设置管理员
   - 配置白名单
   - 暂停功能测试
   
3. 功能验证:
   - 基础功能测试
   - 异常处理验证
   - 性能测试

监控告警:
  - 异常交易监控
  - 余额变动告警
  - Gas价格监控
  - 合约调用频率
```

## 5. 前端集成

### 5.1 钱包集成
```yaml
支持钱包:
  - MetaMask
  - WalletConnect
  - Coinbase Wallet
  
功能实现:
  - 钱包连接
  - 签名验证
  - 交易确认
  - 余额查询
```

### 5.2 用户界面
```yaml
功能模块:
  - 代币余额展示
  - 奖励进度条
  - 提现界面
  - 交易历史
  
交互优化:
  - 实时更新
  - 交易确认
  - 错误处理
  - 加载状态

安全考虑:
  - 私钥本地存储
  - 签名验证
  - 交易确认
  - 金额验证
``` 