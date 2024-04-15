# JWT

[![Lines-of-Code](https://tokei.rs/b1/github/ilyalisov/jwt)](https://github.com/ilyalisov/jwt)
[![Hits-of-Code](https://hitsofcode.com/github/ilyalisov/jwt?branch=master)](https://hitsofcode.com/github/ilyalisov/jwt/view?branch=master)
[![mvn](https://github.com/ilyalisov/jwt/actions/workflows/maven-build.yml/badge.svg)](https://github.com/ilyalisov/jwt/actions/workflows/maven-build.yml)

[![codecov](https://codecov.io/gh/IlyaLisov/jwt/graph/badge.svg?token=OJR6TFQ2qr)](https://codecov.io/gh/IlyaLisov/jwt)

This repository is an open-source Java library for fast and convenient using of
JWT tokens in your Java applications.

## Content:

* [How to use](#how-to-use)
    * [Instantiate a service](#instantiate-a-service)
    * [Persist tokens](#persist-tokens)
    * [Invalidate token](#invalidate-jwt-token)
    * [Create token](#create-jwt-token)
    * [If token is expired](#check-if-jwt-token-is-expired)
    * [If token has claim](#check-if-jwt-token-has-claim)
    * [Get subject from token](#get-subject-from-jwt-token)
    * [Get type from token](#get-type-from-jwt-token)
    * [Get claims from token](#get-claims-from-jwt-token)
    * [Get claim from tokem](#get-claim-from-jwt-token)
* [How to contribute](#how-to-contribute)

## How to use

At first, you need to install this library.

With Maven add dependency to your `pom.xml`.

```xml

<dependency>
    <groupId>io.github.ilyalisov</groupId>
    <artifactId>jwt</artifactId>
    <version>0.2.0</version>
</dependency>
```

This library provides simple and convenient usage.

### Instantiate a service

You need to create `TokenService` object and pass `secret` (base64 encoded
secret string for
JWT tokens) to the constructor.

```java
public class Main {
    public static void main(String[] args) {
        String secret = "c29tZWxvbmdzZWNyZXRzdHJpbmdmb3JleGFtcGxlYW5kaXRuZWVkc3RvYmVsb25nDQo=";

        TokenService tokenService = new TokenServiceImpl(secret);
    }
}
```

After, you can call available methods and use library.

### Persist tokens

Library supports `PersistentTokenService` implementation with saving
tokens to `TokenStorage`.

With such approach you can store tokens in Redis or in-memory Map and create new
one if no specified tokens exist, otherwise, stored JWT token would be returned.

This approach allows you to invalidate created and stored JWT token.

For this look at `TokenStorage` class. Use `TokenStorageImpl` for in-memory
storage (default) and `RedisTokenStorageImpl` for Redis storage.

```java
public class Main {
    public static void main(String[] args) {
        String secret = "c29tZWxvbmdzZWNyZXRzdHJpbmdmb3JleGFtcGxlYW5kaXRuZWVkc3RvYmVsb25nDQo=";

        TokenService tokenService = new PersistentTokenServiceImpl(secret);
    }
}
```

With Redis you need to pass `RedisTokenStorageImpl` object to constructor.

To create `RedisTokenStorageImpl` you need to pass `JedisPool` / host and port /
host, port, username and password.

```java
public class Main {
    public static void main(String[] args) {
        String secret = "c29tZWxvbmdzZWNyZXRzdHJpbmdmb3JleGFtcGxlYW5kaXRuZWVkc3RvYmVsb25nDQo=";
        String host = "localhost";
        int port = 6379;

        TokenStorage tokenStorage = new RedisTokenStorageImpl(
                host,
                port
        );

        PersistentTokenService tokenService = new PersistentTokenServiceImpl(
                secret,
                tokenStorage
        );
    }
}
```

You can choose your own `RedisSchema` which is used to generate a Redis key for
JWT token. Just pass it as argument in `RedisTokenStorageImpl` constructor.

By default, library uses key `"tokens:" + subject + ":" + type`.

### Invalidate JWT token

With `PersistentTokenService` you can invalidate token by token itself or by
subject and token type. If first option is chosen, all keys with such token
values will be deleted.

If token will be deleted from storage you receive `true`.

```java
public class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        boolean deleted = persistentTokenService.invalidate(token);

        System.out.println(deleted);
    }
}
```

```java
public class Main {
    public static void main(String[] args) {
        boolean deleted = persistentTokenService.invalidate(
                TokenParameters.builder(
                                "user@example.com",
                                "access",
                                Duration.of(1, ChronoUnit.HOURS)
                        )
                        .build()
        );

        System.out.println(deleted);
    }
}
```

### Create JWT token

To create token call method `create(TokenParameters params)` on `TokenService`
object.

```java
public class Main {
    public static void main(String[] args) {
        String token = tokenService.create(
                TokenParameters.builder(
                                "user@example.com",
                                "access",
                                Duration.of(1, ChronoUnit.HOURS)
                        )
                        .build()
        );

        System.out.println(token);
    }
}
```

You can specify in `TokenParameters`:

* claims to be put in JWT token
* JWT token issuing date
* JWT token expiration date
* "sub" of JWT token
* type of JWT token

This all is configured via `TokenParameters` builder.

### Check if JWT token is expired

To check if JWT token is expired call method `isExpired(String token)`
on `TokenService` object.

```java
class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        boolean expired = tokenService.isExpired(token);

        System.out.println(expired);
    }
}
```

You can also check expiration with any other date.

```java
class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        Date date = new Date(1705911211182);

        boolean expired = tokenService.isExpired(
                token,
                date
        );

        System.out.println(expired);
    }
}
```

### Check if JWT token has claim

To check if JWT token has claim in payload call
method `has(String token, String key, Object value)` on `TokenService` object.

```java
class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";
        String key = "subject";
        String value = "1234567890";

        boolean hasSubject = tokenService.has(
                token,
                key,
                value
        );

        System.out.println(hasSubject);
    }
}
```

### Get subject from JWT token

To get subject from JWT token payload call method `getSubject(String token)`
on `TokenService` object.

**Note:** Optionally, you can call
method `claims(token).get("sub").toString()` on `TokenService` object.

```java
public class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        String subject = tokenService.getSubject(token);

        System.out.println(subject);
    }
}
```

### Get type from JWT token

To get type from JWT token payload call method `getType(String token)`
on `TokenService` object.

```java
public class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        String subject = tokenService.getType(token);

        System.out.println(subject);
    }
}
```

### Get claims from JWT token

To get all claims from JWT token payload call method `claims(String token)`
on `TokenService`
object.

```java
public class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        Map<String, Object> claims = tokenService.claims(token);

        claims.forEach((key, value) -> System.out.println(key + " " + value));
    }
}

```

### Get claim from JWT token

To get claim by its name from JWT token payload call method `claim(String token, String key)`
on `TokenService`
object.

```java
public class Main {
    public static void main(String[] args) {
        String token = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c";

        String claim = (String) tokenService.claim(token, "subject");

        System.out.println(claim);
    }
}

```

## How to contribute

See active issues at [issues page](https://github.com/ilyalisov/jwt/issues)
