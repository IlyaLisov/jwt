# JWT Java Library

[![Maven Build](https://github.com/ilyalisov/jwt/actions/workflows/maven-publish.yml/badge.svg)](https://github.com/ilyalisov/jwt/actions/workflows/maven-publish.yml)
[![codecov](https://codecov.io/gh/ilyalisov/jwt/branch/main/graph/badge.svg)](https://codecov.io/gh/ilyalisov/jwt)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
![Maven Central](https://img.shields.io/maven-central/v/io.github.ilyalisov/jwt)

A lightweight, production-ready Java library for JWT token management with built-in Redis support, token persistence, and comprehensive validation features.

## Table of Contents
- [Features](#features)
- [Quick Start](#quick-start)
  - [Installation](#installation)
- [Core Components](#core-components)
  - [Token Service](#token-service)
  - [Token Storage](#token-storage)
  - [Token Parameters](#token-parameters)
- [Usage Examples](#usage-examples)
  - [Basic Token Operations](#basic-token-operations)
  - [Persistent Token Storage](#persistent-token-storage)
  - [Redis Integration](#redis-integration)
  - [Token Validation](#token-validation)
- [API Reference](#api-reference)
- [Configuration](#configuration)
- [License](#license)
- [Contributing](#contributing)

## Features
- **Simple JWT Management**: Create, validate, and parse JWT tokens with minimal configuration
- **Persistent Storage**: Built-in support for in-memory and Redis token storage
- **Token Invalidation**: Programmatic token revocation with multiple invalidation strategies
- **Comprehensive Validation**: Check expiration, claims, subjects, and token types
- **Flexible Token Parameters**: Custom claims, issuance dates, and expiration configurations
- **Production Ready**: Thread-safe implementations with comprehensive test coverage
- **Zero Dependencies**: Self-contained with minimal external dependencies (except Redis client)

## Quick Start

### Installation

**Maven:**
```xml
<dependency>
    <groupId>io.github.ilyalisov</groupId>
    <artifactId>jwt</artifactId>
    <version>0.3.0</version>
</dependency>
```

## Core Components

### Token Service
The core interface for JWT operations with two implementations:

**TokenServiceImpl** - Basic JWT operations without persistence:
```java
String secret = "c29tZWxvbmdzZWNyZXRzdHJpbmdmb3JleGFtcGxlYW5kaXRuZWVkc3RvYmVsb25nDQo=";
TokenService tokenService = new TokenServiceImpl(secret);
```

**PersistentTokenServiceImpl** - JWT operations with token storage and invalidation:
```java
TokenService tokenService = new PersistentTokenServiceImpl(secret);
```

### Token Storage
Flexible storage implementations for different use cases:

- **TokenStorageImpl**: In-memory storage (default)
- **RedisTokenStorageImpl**: Redis-backed persistent storage
- **Custom implementations**: Implement `TokenStorage` interface for custom storage solutions

### Token Parameters
Builder pattern for configuring JWT tokens:

```java
TokenParameters params = TokenParameters.builder(
        "user@example.com",      // subject
        "access",                // token type
        Duration.ofHours(1)      // expiration
    )
    .claim("role", "ADMIN")      // custom claims
    .issuedAt(new Date())        // issuance date
    .build();
```

## Usage Examples

### Basic Token Operations

**Create a JWT token:**
```java
String token = tokenService.create(
    TokenParameters.builder(
            "user@example.com",
            "access",
            Duration.ofHours(1)
        )
        .claim("role", "ADMIN")
        .build()
);
```

**Validate token expiration:**
```java
boolean expired = tokenService.isExpired(token);
// Or with custom validation date
boolean expired = tokenService.isExpired(token, new Date());
```

**Check token claims:**
```java
boolean hasRole = tokenService.has(token, "role", "ADMIN");
```

**Extract token information:**
```java
String subject = tokenService.getSubject(token);
String type = tokenService.getType(token);
Map<String, Object> claims = tokenService.claims(token);
Object role = tokenService.claim(token, "role");
```

### Persistent Token Storage

**In-memory storage (default):**
```java
PersistentTokenService tokenService = new PersistentTokenServiceImpl(secret);
```

**Invalidate tokens:**
```java
// Invalidate by token value
boolean deleted = tokenService.invalidate(token);

// Invalidate by subject and type
boolean deleted = tokenService.invalidate(
    TokenParameters.builder(
            "user@example.com",
            "access",
            Duration.ZERO
        )
        .build()
);
```

### Redis Integration

**Basic Redis setup:**
```java
TokenStorage tokenStorage = new RedisTokenStorageImpl("localhost", 6379);
PersistentTokenService tokenService = new PersistentTokenServiceImpl(secret, tokenStorage);
```

**Advanced Redis configuration:**
```java
// With authentication
TokenStorage tokenStorage = new RedisTokenStorageImpl(
    "localhost", 
    6379, 
    "username", 
    "password"
);

// With custom Redis key schema
RedisSchema customSchema = (subject, type) -> "app:tokens:" + subject + ":" + type;
TokenStorage tokenStorage = new RedisTokenStorageImpl(
    "localhost", 
    6379, 
    customSchema
);

// With existing Jedis pool
JedisPool jedisPool = new JedisPool("localhost", 6379);
TokenStorage tokenStorage = new RedisTokenStorageImpl(jedisPool);
```

### Token Validation

**Complete validation workflow:**
```java
public boolean validateToken(String token) {
    // Check if token exists (for persistent storage)
    if (tokenService instanceof PersistentTokenService) {
        if (!((PersistentTokenService) tokenService).exists(token)) {
            return false;
        }
    }
    
    // Validate expiration
    if (tokenService.isExpired(token)) {
        return false;
    }
    
    // Validate required claims
    if (!tokenService.has(token, "role", "USER")) {
        return false;
    }
    
    // Validate token type
    if (!"access".equals(tokenService.getType(token))) {
        return false;
    }
    
    return true;
}
```

## API Reference

### TokenService Interface
| Method | Description | Returns |
|--------|-------------|---------|
| `create(TokenParameters)` | Creates a new JWT token | `String` |
| `isExpired(String)` | Checks if token is expired | `boolean` |
| `isExpired(String, Date)` | Checks if token was expired at specific date | `boolean` |
| `has(String, String, Object)` | Checks if token contains specific claim | `boolean` |
| `getSubject(String)` | Extracts subject from token | `String` |
| `getType(String)` | Extracts type from token | `String` |
| `claims(String)` | Returns all token claims | `Map<String, Object>` |
| `claim(String, String)` | Returns specific claim | `Object` |

### PersistentTokenService Interface (extends TokenService)
| Method | Description | Returns |
|--------|-------------|---------|
| `invalidate(String)` | Invalidates token by value | `boolean` |
| `invalidate(TokenParameters)` | Invalidates token by subject and type | `boolean` |
| `exists(String)` | Checks if token exists in storage | `boolean` |

## Configuration

### Secret Key
The library requires a Base64-encoded secret string. Generate one using:
```java
import java.util.Base64;

String secret = Base64.getEncoder().encodeToString(
    "some-long-secret-string-for-example-and-it-needs-to-be-long".getBytes()
);
```

### Redis Configuration
For production Redis deployments, consider:
- Connection pooling configuration
- SSL/TLS encryption
- Redis cluster support
- Sentinel configuration for high availability

### Token Expiration Strategies
```java
// Short-lived access tokens
Duration accessTokenExpiry = Duration.ofMinutes(30);

// Long-lived refresh tokens
Duration refreshTokenExpiry = Duration.ofDays(7);

// One-time use tokens
Duration oneTimeTokenExpiry = Duration.ofMinutes(10);
```

## License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## Contributing
We welcome contributions! Please feel free to submit issues and enhancement requests.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

**Active Issues**: Check our [issues page](https://github.com/ilyalisov/jwt/issues) for current tasks and bugs.
