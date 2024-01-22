package io.github.ilyalisov.jwt.service;

import io.github.ilyalisov.jwt.config.TokenParameters;
import io.github.ilyalisov.jwt.fake.FakeTokenStorageImpl;
import io.github.ilyalisov.jwt.storage.TokenStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersistentTokenServiceImplTests {

    private static final String SECRET_KEY = "c29tZWxvbmdzZWNyZXRzdHJpbmdmb3JleGFtcGxlYW5kaXRuZWVkc3RvYmVsb25nDQo=";
    private static PersistentTokenServiceImpl tokenService;

    @BeforeEach
    void setup() {
        TokenStorage tokenStorage = new FakeTokenStorageImpl();
        tokenService = new PersistentTokenServiceImpl(
                SECRET_KEY,
                tokenStorage
        );
    }

    @Test
    void shouldGenerateValidToken() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        assertNotNull(token);
        assertEquals(subject, tokenService.getSubject(token));
    }

    @Test
    void withExistingTokenShouldReturnExistingToken() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String existingToken = tokenService.create(params);

        String token = tokenService.create(params);

        assertEquals(existingToken, token);
    }

    @Test
    void withValidTokenShouldReturnFalse() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        assertFalse(tokenService.isExpired(token));
    }

    @Test
    void withExpiredTokenShouldReturnTrue() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofSeconds(1);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String expiredToken = tokenService.create(params);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        assertTrue(tokenService.isExpired(expiredToken));
    }

    @Test
    void withValidClaimShouldReturnTrue() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .claim("testKey", "testValue")
                .build();
        String token = tokenService.create(params);

        assertTrue(tokenService.has(token, "testKey", "testValue"));
    }

    @Test
    void withInvalidClaimShouldReturnFalse() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .claim("testKey", "testValue")
                .build();
        String token = tokenService.create(params);

        assertFalse(tokenService.has(token, "testKey", "invalidValue"));
    }

    @Test
    void shouldReturnCorrectSubject() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        assertEquals(subject, tokenService.getSubject(token));
    }

    @Test
    void shouldReturnCorrectType() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        assertEquals(type, tokenService.getType(token));
    }

    @Test
    void shouldReturnCorrectClaims() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);
        Map<String, Object> customClaims = new HashMap<>();
        customClaims.put("key1", "value1");
        customClaims.put("key2", 123);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .claims(customClaims)
                .build();
        String token = tokenService.create(params);

        Map<String, Object> claims = tokenService.claims(token);

        assertNotNull(claims);
        assertEquals("value1", claims.get("key1"));
        assertEquals(123, claims.get("key2"));
    }

    @Test
    void shouldInvalidateByToken() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        tokenService.invalidate(token);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TokenParameters newParams = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String newToken = tokenService.create(newParams);
        assertNotEquals(token, newToken);
    }

    @Test
    void shouldInvalidateBySubjectAndType() {
        String subject = "testSubject";
        String type = "any";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters params = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String token = tokenService.create(params);

        tokenService.invalidate(params);

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        TokenParameters newParams = TokenParameters.builder(
                        subject,
                        type,
                        duration
                )
                .build();
        String newToken = tokenService.create(newParams);
        assertNotEquals(token, newToken);
    }

}
