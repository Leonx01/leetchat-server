# LeetChat - 实时聊天后端

基于 Spring Boot 构建的实时聊天后端，支持私聊、WebSocket 通信、文件上传和好友管理。

[![Java 8](https://img.shields.io/badge/Java-8-blue)](https://www.java.com)
[![Spring Boot 2.7.6](https://img.shields.io/badge/Spring%20Boot-2.7.6-green)](https://spring.io/projects/spring-boot)
[![MySQL 8](https://img.shields.io/badge/MySQL-8-orange)](https://www.mysql.com)
[![Redis](https://img.shields.io/badge/Redis-Required-red)](https://redis.io)

## 功能特性

- **用户认证**：基于 JWT 的身份验证，支持电子邮件验证
- **好友系统**：发送/接收好友请求，管理好友列表
- **实时消息**：基于 WebSocket 的私聊，支持消息持久化
- **在线状态**：实时用户在线/离线状态追踪
- **文件上传**：通过阿里云 OSS 支持头像和文件上传
- **WebRTC 信令**：内置语音/视频通话信令服务器
- **敏感词过滤**：聊天消息内容审核

## 技术栈

- **框架**：Spring Boot 2.7.6
- **数据库**：MySQL 8 + MyBatis-Plus 3.5.2
- **缓存**：Redis
- **实时通信**：WebSocket (JSR-356)
- **身份验证**：JWT (JSON Web Token)
- **文件存储**：阿里云 OSS
- **API 文档**：Swagger 2
- **构建工具**：Maven

## 快速开始

### 前置条件

- Java 8 或更高版本
- MySQL 8.0+
- Redis 5.0+
- Maven 3.6+

### 1. 克隆仓库

```bash
git clone https://github.com/yourusername/leetchat_springboot.git
cd leetchat_springboot
```

### 2. 数据库设置

创建一个名为 `discord` 的 MySQL 数据库（或您偏好的名称）：

```sql
CREATE DATABASE discord CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. 配置

复制示例配置并使用您的设置进行更新：

```bash
cp src/main/resources/application-example.yml src/main/resources/application-local.yml
```

编辑 `application-local.yml`，包含您的实际凭据：

- MySQL 连接设置
- Redis 连接设置
- 电子邮件 SMTP 设置（用于验证码）
- 阿里云 OSS 凭据（用于文件上传）
- JWT 密钥

### 4. 运行应用

#### 选项 1：使用 Maven

```bash
# 使用 local 配置文件运行
mvn spring-boot:run -Dspring-boot.run.profiles=local

# 或直接运行
mvn spring-boot:run
```

#### 选项 2：使用环境变量

```bash
export MYSQL_URL=jdbc:mysql://localhost:3306/discord
export MYSQL_USERNAME=root
export MYSQL_PASSWORD=your-password
export REDIS_HOST=127.0.0.1
export REDIS_PORT=6379
export JWT_SECRET=your-secret-key
export MAIL_USERNAME=your-email@163.com
export MAIL_PASSWORD=your-email-password
export OSS_ACCESS_KEY_ID=your-access-key
export OSS_ACCESS_KEY_SECRET=your-secret-key
export OSS_BUCKET_NAME=your-bucket

mvn spring-boot:run
```

### 5. 访问应用

- REST API：`http://localhost:8080`
- API 文档：`http://localhost:8080/swagger-ui.html`

## API 概览

### 身份验证

| 端点 | 方法 | 描述 | 需要认证 |
|------|------|------|--------|
| `/api/login` | POST | 用户登录 | 否 |
| `/api/register` | POST | 用户注册 | 否 |
| `/api/verify-code` | POST | 发送电子邮件验证码 | 否 |
| `/api/reset-password` | POST | 重置密码 | 否 |

### 用户管理

| 端点 | 方法 | 描述 | 需要认证 |
|------|------|------|--------|
| `/api/user/info` | GET | 获取当前用户信息 | 是 |
| `/api/user/update` | POST | 更新用户信息 | 是 |
| `/api/user/avatar` | POST | 上传头像 | 是 |

### 好友系统

| 端点 | 方法 | 描述 | 需要认证 |
|------|------|------|--------|
| `/api/friend/list` | GET | 获取好友列表 | 是 |
| `/api/friend/request` | POST | 发送好友请求 | 是 |
| `/api/friend/request/list` | GET | 获取好友请求 | 是 |
| `/api/friend/accept` | POST | 接受好友请求 | 是 |

### 消息

| 端点 | 方法 | 描述 | 需要认证 |
|------|------|------|--------|
| `/api/message/history` | GET | 获取聊天历史 | 是 |
| `/api/message/windows` | GET | 获取聊天窗口 | 是 |

### WebSocket 端点

| 端点 | 描述 |
|------|------|
| `ws://localhost:8080/private/{sid}` | 私聊频道 |
| `ws://localhost:8080/status/{uid}` | 在线状态和信令频道 |

**WebSocket 连接**：在 `Sec-WebSocket-Protocol` 请求头中传递 JWT 令牌。

## 项目结构

```
leetchat_springboot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nileonx/leetchat_springboot/
│   │   │       ├── config/          # 配置类
│   │   │       ├── controller/      # REST 控制器
│   │   │       ├── entities/        # 数据库实体
│   │   │       ├── exception/       # 异常处理器
│   │   │       ├── interceptor/     # JWT 拦截器
│   │   │       ├── mapper/          # MyBatis 映射器
│   │   │       ├── server/          # WebSocket 服务器
│   │   │       ├── service/         # 业务逻辑
│   │   │       ├── utils/           # 工具类
│   │   │       └── LeetchatSpringbootApplication.java
│   │   └── resources/
│   │       ├── application.yml      # 主配置
│   │       ├── application-example.yml  # 示例配置
│   │       └── mapper/              # MyBatis XML 映射文件
│   └── test/                        # 测试类
├── pom.xml                          # Maven 配置
└── README.md                        # 本文件
```

## 配置参考

所有敏感配置都可以通过环境变量设置：

| 变量 | 描述 | 必需 |
|------|------|------|
| `MYSQL_URL` | MySQL JDBC URL | 是 |
| `MYSQL_USERNAME` | MySQL 用户名 | 是 |
| `MYSQL_PASSWORD` | MySQL 密码 | 是 |
| `REDIS_HOST` | Redis 主机 | 是（默认：127.0.0.1） |
| `REDIS_PORT` | Redis 端口 | 是（默认：6379） |
| `REDIS_PASSWORD` | Redis 密码 | 否 |
| `JWT_SECRET` | JWT 签名密钥 | 是 |
| `MAIL_HOST` | SMTP 主机 | 是（默认：smtp.163.com） |
| `MAIL_USERNAME` | SMTP 用户名 | 是 |
| `MAIL_PASSWORD` | SMTP 密码 | 是 |
| `OSS_ENDPOINT` | 阿里云 OSS 端点 | 是（文件上传） |
| `OSS_ACCESS_KEY_ID` | OSS 访问密钥 ID | 是（文件上传） |
| `OSS_ACCESS_KEY_SECRET` | OSS 访问密钥 | 是（文件上传） |
| `OSS_BUCKET_NAME` | OSS 桶名称 | 是（文件上传） |

## 开发

### 构建项目

```bash
# 编译
mvn clean compile

# 运行测试
mvn test

# 打包成 JAR
mvn clean package

# 跳过测试构建
mvn clean package -DskipTests
```

### 运行单个测试

```bash
# 运行特定测试类
mvn test -Dtest=ClassName

# 运行特定测试方法
mvn test -Dtest=ClassName#methodName
```

### 数据库模式

应用程序使用 MyBatis-Plus 作为 ORM。`entities/` 包中的实体类定义数据库模式。关键表：

- `user`：用户账户
- `friend`：好友关系
- `friend_request`：待处理的好友请求
- `private_server`：聊天会话
- `privatemessage`：聊天消息
- `window`：用户聊天窗口
- `file`：文件元数据

## 部署

### Docker（可选）

您可以将应用程序容器化：

```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/leetchat_springboot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### 生产环境考虑事项

1. **安全性**：将默认 JWT 密钥更改为强随机字符串
2. **数据库**：使用连接池并配置适当的池大小
3. **Redis**：启用持久化并配置适当的内存限制
4. **HTTPS**：为安全通信配置 SSL 证书
5. **CORS**：在生产环境中配置允许的源

## 贡献

1. Fork 该仓库
2. 创建功能分支 (`git checkout -b feature/amazing-feature`)
3. 提交您的更改 (`git commit -m 'Add amazing feature'`)
4. 推送到分支 (`git push origin feature/amazing-feature`)
5. 开启 Pull Request

## 许可证

本项目采用 MIT 许可证 - 详见 [LICENSE](LICENSE) 文件。

## 鸣谢

- 采用 [Spring Boot](https://spring.io/projects/spring-boot) 构建
- ORM 由 [MyBatis-Plus](https://baomidou.com/) 提供支持
- 文件存储由 [阿里云 OSS](https://www.aliyun.com/product/oss) 提供
