# LeetChat - Real-time Chat Backend

A real-time chat backend built with Spring Boot, supporting private messaging, WebSocket communication, file upload, and friend management.

[![Java 8](https://img.shields.io/badge/Java-8-blue)](https://www.java.com)
[![Spring Boot 2.7.6](https://img.shields.io/badge/Spring%20Boot-2.7.6-green)](https://spring.io/projects/spring-boot)
[![MySQL 8](https://img.shields.io/badge/MySQL-8-orange)](https://www.mysql.com)
[![Redis](https://img.shields.io/badge/Redis-Required-red)](https://redis.io)

## Features

- **User Authentication**: JWT-based authentication with email verification
- **Friend System**: Send/receive friend requests, manage friend list
- **Real-time Messaging**: WebSocket-based private chat with message persistence
- **Online Status**: Real-time user online/offline status tracking
- **File Upload**: Support for avatar and file uploads via Aliyun OSS
- **WebRTC Signaling**: Built-in signaling server for voice/video calls
- **Sensitive Word Filtering**: Content moderation for chat messages

## Tech Stack

- **Framework**: Spring Boot 2.7.6
- **Database**: MySQL 8 + MyBatis-Plus 3.5.2
- **Cache**: Redis
- **Real-time Communication**: WebSocket (JSR-356)
- **Authentication**: JWT (JSON Web Token)
- **File Storage**: Aliyun OSS
- **API Documentation**: Swagger 2
- **Build Tool**: Maven

## Quick Start

### Prerequisites

- Java 8 or higher
- MySQL 8.0+
- Redis 5.0+
- Maven 3.6+

### 1. Clone the Repository

```bash
git clone https://github.com/yourusername/leetchat_springboot.git
cd leetchat_springboot
```

### 2. Database Setup

Create a MySQL database named `discord` (or any name you prefer):

```sql
CREATE DATABASE discord CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

### 3. Configuration

Copy the example configuration and update with your settings:

```bash
cp src/main/resources/application-example.yml src/main/resources/application-local.yml
```

Edit `application-local.yml` with your actual credentials:

- MySQL connection settings
- Redis connection settings
- Email SMTP settings (for verification codes)
- Aliyun OSS credentials (for file upload)
- JWT secret key

### 4. Run the Application

#### Option 1: Using Maven

```bash
# Run with local profile
mvn spring-boot:run -Dspring-boot.run.profiles=local

# Or run directly
mvn spring-boot:run
```

#### Option 2: Using Environment Variables

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

### 5. Access the Application

- REST API: `http://localhost:8080`
- API Documentation: `http://localhost:8080/swagger-ui.html`

## API Overview

### Authentication

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/login` | POST | User login | No |
| `/api/register` | POST | User registration | No |
| `/api/verify-code` | POST | Send email verification code | No |
| `/api/reset-password` | POST | Reset password | No |

### User Management

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/user/info` | GET | Get current user info | Yes |
| `/api/user/update` | POST | Update user info | Yes |
| `/api/user/avatar` | POST | Upload avatar | Yes |

### Friend System

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/friend/list` | GET | Get friend list | Yes |
| `/api/friend/request` | POST | Send friend request | Yes |
| `/api/friend/request/list` | GET | Get friend requests | Yes |
| `/api/friend/accept` | POST | Accept friend request | Yes |

### Messaging

| Endpoint | Method | Description | Auth Required |
|----------|--------|-------------|---------------|
| `/api/message/history` | GET | Get chat history | Yes |
| `/api/message/windows` | GET | Get chat windows | Yes |

### WebSocket Endpoints

| Endpoint | Description |
|----------|-------------|
| `ws://localhost:8080/private/{sid}` | Private chat channel |
| `ws://localhost:8080/status/{uid}` | Online status & signaling channel |

**WebSocket Connection**: Pass JWT token in `Sec-WebSocket-Protocol` header.

## Project Structure

```
leetchat_springboot/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/nileonx/leetchat_springboot/
│   │   │       ├── config/          # Configuration classes
│   │   │       ├── controller/      # REST controllers
│   │   │       ├── entities/        # Database entities
│   │   │       ├── exception/       # Exception handlers
│   │   │       ├── interceptor/     # JWT interceptors
│   │   │       ├── mapper/          # MyBatis mappers
│   │   │       ├── server/          # WebSocket servers
│   │   │       ├── service/         # Business logic
│   │   │       ├── utils/           # Utility classes
│   │   │       └── LeetchatSpringbootApplication.java
│   │   └── resources/
│   │       ├── application.yml      # Main configuration
│   │       ├── application-example.yml  # Example config
│   │       └── mapper/              # MyBatis XML mappers
│   └── test/                        # Test classes
├── pom.xml                          # Maven configuration
└── README.md                        # This file
```

## Configuration Reference

All sensitive configurations can be set via environment variables:

| Variable | Description | Required |
|----------|-------------|----------|
| `MYSQL_URL` | MySQL JDBC URL | Yes |
| `MYSQL_USERNAME` | MySQL username | Yes |
| `MYSQL_PASSWORD` | MySQL password | Yes |
| `REDIS_HOST` | Redis host | Yes (default: 127.0.0.1) |
| `REDIS_PORT` | Redis port | Yes (default: 6379) |
| `REDIS_PASSWORD` | Redis password | No |
| `JWT_SECRET` | JWT signing key | Yes |
| `MAIL_HOST` | SMTP host | Yes (default: smtp.163.com) |
| `MAIL_USERNAME` | SMTP username | Yes |
| `MAIL_PASSWORD` | SMTP password | Yes |
| `OSS_ENDPOINT` | Aliyun OSS endpoint | Yes (for file upload) |
| `OSS_ACCESS_KEY_ID` | OSS Access Key ID | Yes (for file upload) |
| `OSS_ACCESS_KEY_SECRET` | OSS Access Key Secret | Yes (for file upload) |
| `OSS_BUCKET_NAME` | OSS bucket name | Yes (for file upload) |

## Development

### Building the Project

```bash
# Compile
mvn clean compile

# Run tests
mvn test

# Package as JAR
mvn clean package

# Skip tests during build
mvn clean package -DskipTests
```

### Running a Single Test

```bash
# Run a specific test class
mvn test -Dtest=ClassName

# Run a specific test method
mvn test -Dtest=ClassName#methodName
```

### Database Schema

The application uses MyBatis-Plus for ORM. Entity classes in `entities/` package define the database schema. Key tables:

- `user`: User accounts
- `friend`: Friend relationships
- `friend_request`: Pending friend requests
- `private_server`: Chat sessions
- `privatemessage`: Chat messages
- `window`: User chat windows
- `file`: File metadata

## Deployment

### Docker (Optional)

You can containerize the application:

```dockerfile
FROM openjdk:8-jdk-alpine
COPY target/leetchat_springboot-0.0.1-SNAPSHOT.jar app.jar
ENTRYPOINT ["java","-jar","/app.jar"]
```

### Production Considerations

1. **Security**: Change the default JWT secret to a strong random string
2. **Database**: Use connection pooling and configure appropriate pool sizes
3. **Redis**: Enable persistence and configure appropriate memory limits
4. **HTTPS**: Configure SSL certificates for secure communication
5. **CORS**: Configure allowed origins in production

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Acknowledgments

- Built with [Spring Boot](https://spring.io/projects/spring-boot)
- ORM powered by [MyBatis-Plus](https://baomidou.com/)
- File storage by [Aliyun OSS](https://www.aliyun.com/product/oss)
