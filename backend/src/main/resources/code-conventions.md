# 代码规范文档

## 1. 包结构规范

```
com.aimusic.backend/
├── domain/           # 领域模型
│   ├── dto/         # 数据传输对象
│   ├── entity/      # 实体类
│   ├── repository/  # 数据访问层
│   └── service/     # 业务逻辑层
├── controller/      # 控制器层
├── config/         # 配置类
├── aspect/         # 切面类
└── exception/      # 异常处理
```

## 2. 命名规范

### 类命名
- 使用 PascalCase
- 具体类：`MusicService`, `MusicController`
- 抽象类：`AbstractEntity`, `BaseService`
- 接口：`MusicRepository`, `FileStorage`

### 方法命名
- 使用 camelCase
- 动词开头：`createMusic`, `updateStatus`
- 布尔方法：`isValid`, `hasPermission`
- 获取方法：`getMusicById`, `findByTitle`

### 变量命名
- 使用 camelCase
- 常量：`MAX_RETRY_COUNT`
- 集合：`musicList`, `userSet`
- 布尔值：`isEnabled`, `hasData`

## 3. 注释规范

### 类注释
```java
/**
 * 音乐服务实现类
 * 处理音乐生成、查询、更新等业务逻辑
 *
 * @author AI Music Team
 * @version 1.0.0
 */
```

### 方法注释
```java
/**
 * 根据ID查询音乐
 *
 * @param id 音乐ID
 * @return 音乐信息
 * @throws EntityNotFoundException 当音乐不存在时
 */
```

### 字段注释
```java
/** 最大重试次数 */
private static final int MAX_RETRY_COUNT = 3;
```

## 4. 异常处理规范

### 自定义异常
```java
public class EntityNotFoundException extends RuntimeException {
    public EntityNotFoundException(String message) {
        super(message);
    }
}
```

### 异常处理器
```java
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleEntityNotFound(EntityNotFoundException ex) {
        // 处理逻辑
    }
}
```

## 5. API设计规范

### URL命名
- 使用名词复数：`/api/v1/musics`
- 资源层级：`/api/v1/users/{userId}/musics`
- 查询参数：`/api/v1/musics?status=COMPLETED`

### HTTP方法
- GET：查询资源
- POST：创建资源
- PUT：更新资源
- DELETE：删除资源

### 响应格式
```json
{
    "code": 200,
    "message": "success",
    "data": {
        "id": "uuid",
        "title": "music title"
    }
}
```

## 6. 日志规范

### 日志级别
- ERROR：异常和错误
- WARN：警告信息
- INFO：重要业务事件
- DEBUG：调试信息

### 日志格式
```java
log.info("Processing music generation request: {}", request);
log.error("Failed to generate music", exception);
```

## 7. 代码格式

### 缩进和空格
- 使用4个空格缩进
- 运算符两边加空格
- 逗号后加空格
- 左大括号前加空格

### 换行
- 方法之间空一行
- 逻辑块之间空一行
- 一行不超过120字符
- 链式调用每行一个方法

### 导入语句
- 不使用通配符导入
- 按类型分组导入
- 去除未使用的导入
- 静态导入单独分组

## 8. 性能规范

### 数据库操作
- 使用分页查询
- 避免N+1查询
- 合理使用索引
- 控制事务范围

### 缓存使用
- 合理使用缓存
- 设置过期时间
- 防止缓存穿透
- 定期清理缓存

### 并发处理
- 使用线程池
- 避免死锁
- 合理设置超时
- 使用并发工具类

## 9. 安全规范

### 输入验证
- 验证所有输入
- 使用参数绑定
- 防止SQL注入
- 防止XSS攻击

### 认证授权
- 使用JWT令牌
- 实现角色权限
- 密码加密存储
- 会话管理 