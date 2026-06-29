# Distributed ID Generator

一个基于 Spring Boot 的分布式ID生成示例项目，包含多种主流ID生成方案。

## 技术栈

- Java 8+
- Spring Boot 2.7.x
- MyBatis-Plus 3.5.x
- Redisson 3.24.x
- H2 Database (内存数据库)

## 支持的ID生成方案

| 方案 | 说明 | 特点 |
|------|------|------|
| **Snowflake** | 雪花算法 | 高性能、分布式唯一、包含时间戳 |
| **Segment** | 号段模式 | 双Buffer预加载、高可用、支持自定义步长 |
| **Redis** | Redis自增 | 原子性保证、分布式唯一、支持批量获取 |
| **UUID** | 标准UUID | 简单、无需依赖、非有序 |

## 项目结构

```
distributed-id/
├── distributed-id-common/        # 公共模块
│   ├── IdGenerator.java          # ID生成器接口
│   └── Result.java               # 统一响应封装
├── distributed-id-snowflake/     # 雪花算法模块
│   ├── SnowflakeGenerator.java   # 雪花算法核心实现
│   └── SnowflakeConfig.java      # 配置类
├── distributed-id-segment/       # 号段模式模块
│   ├── entity/SegmentEntity.java
│   ├── mapper/SegmentMapper.java
│   ├── SegmentGenerator.java     # 号段模式实现（双Buffer）
│   ├── SegmentConfig.java
│   └── resources/schema.sql
├── distributed-id-redis/         # Redis自增模块
│   ├── RedisGenerator.java       # Redis实现（批量获取）
│   └── RedisConfig.java
└── distributed-id-server/        # Web服务入口
    ├── DistributedIdApplication.java
    ├── config/ServerConfig.java
    ├── controller/IdController.java
    └── resources/application.yml
```

## API接口

### 雪花算法

```
GET /api/id/snowflake          # 获取单个ID
GET /api/id/snowflake/batch    # 批量获取ID
GET /api/id/snowflake/benchmark # 性能测试
```

### 号段模式

```
GET /api/id/segment            # 获取单个ID
GET /api/id/segment/batch      # 批量获取ID
```

### Redis自增

```
GET /api/id/redis              # 获取单个ID
GET /api/id/redis/batch        # 批量获取ID
```

### UUID

```
GET /api/id/uuid               # 获取UUID
```

### 对比测试

```
GET /api/id/compare            # 获取所有方案的ID
```

## 启动方式

### 方式一：Maven运行

```bash
# 进入项目目录
cd distributed-id

# 运行服务（Snowflake和Segment方案不需要Redis）
mvn spring-boot:run -pl distributed-id-server
```

### 方式二：打包运行

```bash
# 打包
mvn clean package -DskipTests

# 运行
java -jar distributed-id-server/target/distributed-id-server-1.0.0.jar
```

### Redis方案（可选）

如果需要使用Redis方案，请确保本地已启动Redis服务（默认端口6379）：

```bash
# 启动Redis
redis-server
```

## 配置说明

配置文件位于 `distributed-id-server/src/main/resources/application.yml`：

```yaml
# 雪花算法配置
snowflake:
  worker-id: 0  # 工作节点ID，范围0-1023

# 号段模式配置
segment:
  biz-type: default  # 业务类型
  step: 1000         # 步长

# Redis配置
redis:
  id:
    key: distributed:id:generator
    step: 1000
  host: localhost
  port: 6379
```

## 雪花算法原理

雪花算法生成的ID结构（64位）：

```
0 | timestamp(41位) | workerId(10位) | sequence(12位)
```

- **1位符号位**：始终为0，表示正数
- **41位时间戳**：精确到毫秒，可使用约69年
- **10位工作节点ID**：支持1024个节点
- **12位序列号**：每毫秒可生成4096个ID

## 号段模式原理

号段模式采用双Buffer机制：

1. 从数据库预加载一段ID到内存
2. 使用到一半时，异步加载下一段到Buffer
3. 当前段用完后，切换到Buffer，实现无间断获取

## 性能测试

运行雪花算法性能测试（默认生成10000个ID）：

```bash
curl "http://localhost:8080/api/id/snowflake/benchmark?count=10000"
```

响应示例：
```json
{
  "code": 200,
  "message": "success",
  "data": {
    "count": 10000,
    "time": 15,
    "qps": 666666.67
  }
}
```

## 许可证

MIT License