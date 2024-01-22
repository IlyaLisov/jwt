package io.github.ilyalisov.jwt.storage;

import io.github.ilyalisov.jwt.config.TokenParameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TokenStorageImplTests {

    private TokenStorageImpl tokenStorage;

    @BeforeEach
    void setup() {
        tokenStorage = new TokenStorageImpl();
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
        String token = "testToken";
        tokenStorage.save(
                token,
                params
        );

        tokenStorage.remove(token);

        String existingToken = tokenStorage.get(params);
        assertNull(existingToken);
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
        String token = "testToken";
        tokenStorage.save(
                token,
                params
        );

        tokenStorage.remove(params);

        String existingToken = tokenStorage.get(params);
        assertNull(existingToken);
    }

}

