# 测试规范文档

## 1. 测试分类

### 单元测试
- 使用 `@Test` 注解
- 每个方法只测试一个功能点
- 使用 Mockito 模拟依赖
- 测试命名格式：`should[Expected]_when[Condition]`

### 集成测试
- 使用 `@SpringBootTest` 注解
- 使用 `@Transactional` 保证数据回滚
- 使用实际的数据库连接
- 测试完整的业务流程

### API测试
- 使用 `@WebMvcTest` 注解
- 使用 MockMvc 模拟HTTP请求
- 验证请求响应和状态码
- 测试API参数验证

### 仓库测试
- 使用 `@DataJpaTest` 注解
- 测试数据库操作
- 验证查询结果
- 测试事务行为

## 2. 测试命名规范

### 类命名
- 被测试类名 + Test
- 例：`MusicServiceTest`, `MusicControllerTest`

### 方法命名
- should[预期结果]_when[条件]
- 例：`shouldReturnMusic_whenValidId()`

## 3. 测试结构

### Given-When-Then
```java
@Test
void shouldReturnMusic_whenValidId() {
    // Given
    UUID id = UUID.randomUUID();
    Music music = new Music(id);
    when(repository.findById(id)).thenReturn(Optional.of(music));

    // When
    Music result = service.getMusicById(id);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(id);
}
```

## 4. 测试数据准备

### 测试工厂
```java
public class MusicTestFactory {
    public static Music createMusic() {
        return Music.builder()
                .id(UUID.randomUUID())
                .title("Test Music")
                .status(MusicStatus.COMPLETED)
                .build();
    }
}
```

## 5. 断言最佳实践

### 使用AssertJ
```java
assertThat(music).isNotNull();
assertThat(music.getTitle()).isEqualTo("Test Music");
assertThat(music.getStatus()).isEqualTo(MusicStatus.COMPLETED);
```

## 6. 测试覆盖率要求

- 单元测试覆盖率 > 90%
- 集成测试覆盖关键业务流程
- 异常路径必须测试
- 边界条件必须测试

## 7. 测试环境配置

### application-test.yml
```yaml
spring:
  datasource:
    url: jdbc:h2:mem:testdb
    driver-class-name: org.h2.Driver
  jpa:
    database-platform: org.hibernate.dialect.H2Dialect
```

## 8. Mock使用规范

### Mockito使用
```java
@Mock
private MusicRepository repository;

@InjectMocks
private MusicService service;

@BeforeEach
void setup() {
    MockitoAnnotations.openMocks(this);
}
``` 