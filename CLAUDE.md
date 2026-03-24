# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

LeetChat is a real-time chat backend built with Spring Boot 2.7.6 and Java 8. It provides:
- User authentication (JWT-based) with email verification
- Friend system (requests, approvals, friend lists)
- Private messaging via WebSocket
- File upload/download with Aliyun OSS
- Online status tracking and WebRTC signaling
- Sensitive word filtering

## Build Commands

This is a Maven project. Use these commands:

```bash
# Build the project
mvn clean compile

# Run the application
mvn spring-boot:run

# Run tests
mvn test

# Package as JAR
mvn clean package

# Skip tests during build
mvn clean package -DskipTests
```

The application runs on port 8080 by default (configurable in `application.yml`).

## Architecture

### Layer Structure
- **Controller**: HTTP REST endpoints (`controller/`)
- **Service**: Business logic (`service/`, `service.impl/`)
- **Mapper**: MyBatis-Plus data access (`mapper/`)
- **Entity**: Database entities (`entities/`)
- **VO**: Value objects for API responses (`vo/`)
- **Server**: WebSocket endpoints (`server/`)
- **Config**: Spring configurations (`config/`)
- **Interceptor**: JWT token validation (`interceptor/`)
- **Exception**: Global exception handling (`exception/`)
- **Utils**: Utilities (JWT, Encryption, Mail, etc.)

### Key Technologies
- Spring Boot 2.7.6 with Spring Web, WebSocket, Data Redis
- MyBatis-Plus 3.5.2 for ORM
- MySQL 8+ with Druid connection pool
- Redis for caching (verification codes, download URLs)
- JWT (auth0/java-jwt) for authentication
- Aliyun OSS for file storage
- Swagger 2.7.0 for API documentation
- PageHelper for pagination

### Authentication Flow
1. Login/Register returns JWT token
2. Token passed in `token` HTTP header for REST APIs
3. Token passed in `Sec-WebSocket-Protocol` header for WebSocket connections
4. `@TokenRequired` annotation marks protected endpoints
5. `TokenInterceptor` validates JWT and extracts `uname` (username)

### WebSocket Architecture
Two separate WebSocket servers:

1. **PrivateWebSocket** (`/private/{sid}`): Private chat messages
   - Session ID (`sid`) identifies the chat session
   - Supports text, image, file messages
   - Handles chat history persistence
   - ConcurrentHashMap manages connections per session

2. **GlobalUserStatusServer** (`/status/{uid}`): Online status & signaling
   - User online/offline status broadcasting
   - WebRTC signaling (offer/answer/ice candidates)
   - Heartbeat handling

Both use `ServerEncoder`/`ServerDecoder` for JSON message conversion.

### Database Entities
- `UserEntity`: User accounts (uid, uname, email, avatar, etc.)
- `FriendEntity`: Friend relationships
- `FriendRequestEntity`: Pending friend requests
- `PrivateMsgEntity`: Chat messages
- `PrivateServerEntity`: Chat sessions between user pairs
- `WindowEntity`: User's chat window list
- `FileEntity`: File metadata for uploads

### Configuration Requirements
The application requires these external services configured in `application.yml`:
- MySQL database (configured for local development)
- Redis server (default port 6379)
- SMTP server for email (currently 163.com)
- Aliyun OSS credentials

## Development Notes

### Adding a New API Endpoint
1. Create method in Controller class
2. Add `@TokenRequired` if authentication needed
3. Use `BaseResponse<T>` as return type via `ResultUtil.success()` or `ResultUtil.error()`
4. Access current user via `request.getAttribute("uname")`

### Adding a New WebSocket Message Type
1. Create class extending `BaseMessage` in `entities.ws`
2. Add handling logic in `PrivateWebSocket.onMessage()` or `GlobalUserStatusServer`
3. Update `ServerDecoder` if new message type needs special handling

### Exception Handling
- Throw `BusinessException` with `ErrorCode` for expected errors
- `GlobalExceptionHandler` catches and converts to `BaseResponse`
- Error codes defined in `ErrorCode` enum

### Testing
```bash
# Run a single test class
mvn test -Dtest=ClassName

# Run a specific test method
mvn test -Dtest=ClassName#methodName
```

## Important File Locations

- Main class: `LeetchatSpringbootApplication.java`
- Application config: `src/main/resources/application.yml`
- MyBatis mappers: `src/main/resources/mapper/` (XML files if any)
- Static resources: `src/main/resources/static/`, `templates/`

## Documentation Files

Project documentation is in `.memo/` directory:
- `project.md`: Project overview and interview highlights (in Chinese)
- `TODO.md`: Development roadmap and improvements (in Chinese)
- `websocket.md`, `webrtc.md`: Technical notes
