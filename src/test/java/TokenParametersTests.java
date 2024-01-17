import io.github.ilyalisov.jwt.TokenParameters;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class TokenParametersTests {

    @Test
    void shouldCreate() {
        String subject = "testSubject";
        Duration duration = Duration.ofMinutes(30);

        TokenParameters tokenParameters = TokenParameters.builder(
                        subject,
                        duration
                )
                .build();

        assertNotNull(tokenParameters);
        assertEquals(subject, tokenParameters.getSubject());
        assertNotNull(tokenParameters.getClaims());
        assertNotNull(tokenParameters.getIssuedAt());
        assertNotNull(tokenParameters.getExpiredAt());
    }

    @Test
    void shouldSetIssuedAt() {
        String subject = "testSubject";
        Duration duration = Duration.ofMinutes(30);
        Date issuedAt = new Date();

        TokenParameters tokenParameters = TokenParameters.builder(
                        subject,
                        duration
                )
                .issuedAt(issuedAt)
                .build();

        assertNotNull(tokenParameters);
        assertEquals(issuedAt, tokenParameters.getIssuedAt());
    }

    @Test
    void shouldSetExpiredAt() {
        String subject = "testSubject";
        Duration duration = Duration.ofMinutes(30);
        Date expiredAt = new Date();

        TokenParameters tokenParameters = TokenParameters.builder(
                        subject,
                        duration
                )
                .expiredAt(expiredAt)
                .build();

        assertNotNull(tokenParameters);
        assertEquals(expiredAt, tokenParameters.getExpiredAt());
    }

    @Test
    void shouldSetSubject() {
        String baseSubject = "testSubject";
        Duration duration = Duration.ofMinutes(30);
        String subject = "testSubject";

        TokenParameters tokenParameters = TokenParameters.builder(
                        baseSubject,
                        duration
                )
                .subject(subject)
                .build();

        assertNotNull(tokenParameters);
        assertEquals(subject, tokenParameters.getSubject());
    }

    @Test
    void shouldAddClaim() {
        String subject = "testSubject";
        Duration duration = Duration.ofMinutes(30);
        String key = "testKey";
        String value = "testValue";

        TokenParameters tokenParameters = TokenParameters.builder(
                        subject,
                        duration
                )
                .claim(key, value)
                .build();

        assertNotNull(tokenParameters);
        assertEquals(value, tokenParameters.getClaims().get(key));
    }

    @Test
    void shouldAddClaims() {
        String subject = "testSubject";
        Duration duration = Duration.ofMinutes(30);
        String key1 = "testKey1";
        String value1 = "testValue1";
        String key2 = "testKey2";
        String value2 = "testValue2";
        Map<String, Object> claims = new HashMap<>();
        claims.put(key1, value1);
        claims.put(key2, value2);

        TokenParameters tokenParameters = TokenParameters.builder(
                        subject,
                        duration
                )
                .claims(claims)
                .build();

        assertNotNull(tokenParameters);
        assertEquals(value1, tokenParameters.getClaims().get(key1));
        assertEquals(value2, tokenParameters.getClaims().get(key2));
    }

}
