package io.github.ilyalisov.jwt.storage;

import io.github.ilyalisov.jwt.config.TokenParameters;
import io.github.ilyalisov.jwt.config.redis.DefaultRedisSchema;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Testcontainers
class RedisTokenStorageImplTests {

    private RedisTokenStorageImpl tokenStorage;

    @Container
    public GenericContainer redis = new GenericContainer(
            DockerImageName.parse("redis:5.0.3-alpine")
    )
            .withExposedPorts(6379);

    @BeforeEach
    void setup() {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setJmxEnabled(false);
        JedisPool jedisPool = new JedisPool(
                config,
                redis.getHost(),
                redis.getMappedPort(6379)
        );
        tokenStorage = new RedisTokenStorageImpl(
                jedisPool,
                new DefaultRedisSchema()
        );
    }

    @Test
    void saveShouldStoreToken() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = "testToken";

        tokenStorage.save(token, params);

        assertTrue(tokenStorage.exists(token, params));
    }

    @Test
    void existsWithNonExistingTokenShouldReturnFalse() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String nonExistingToken = "nonExistingToken";

        assertFalse(tokenStorage.exists(nonExistingToken, params));
    }

    @Test
    void existsWithExistingTokenShouldReturnTrue() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = "testToken";

        tokenStorage.save(token, params);

        assertTrue(tokenStorage.exists(token, params));
    }

    @Test
    void getWithExistingTokenShouldReturnToken() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = "testToken";

        tokenStorage.save(token, params);

        assertEquals(token, tokenStorage.get(params));
    }

    @Test
    void getWithNonExistingTokenShouldReturnNull() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();

        assertNull(tokenStorage.get(params));
    }

}
