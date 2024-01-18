package io.github.ilyalisov.jwt;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Parameters for JWT token generation.
 */
@Builder(
        builderMethodName = "hiddenBuilder",
        access = AccessLevel.PRIVATE
)
@Getter
public class TokenParameters {

    /**
     * A map of claims to be put in JWT token.
     */
    private Map<String, Object> claims;

    /**
     * The "sub" of JWT token.
     */
    private String subject;

    /**
     * Date when JWT token was issued.
     */
    private Date issuedAt;

    /**
     * Date when JWT token will be expired.
     */
    private Date expiredAt;

    /**
     * Creates a builder for io.github.ilyalisov.jwt.TokenParameters.
     *
     * @param subject  sub of JWT token
     * @param duration duration between token issuing and expiration date
     * @return TokenParametersBuilder
     */
    public static TokenParametersBuilder builder(
            final String subject,
            final Duration duration
    ) {
        Date issuedAt = new Date();
        return hiddenBuilder()
                .claims(new HashMap<>())
                .issuedAt(issuedAt)
                .subject(subject)
                .expiredAt(new Date(
                        issuedAt.getTime()
                                + 1000 * duration.get(ChronoUnit.SECONDS)
                ));
    }

    public static class TokenParametersBuilder {

        /**
         * Add claims to parameters.
         *
         * @param key   the key of claim
         * @param value the value of claim
         * @return TokenParametersBuilder
         */
        public TokenParametersBuilder claim(
                final String key,
                final Object value
        ) {
            if (this.claims != null) {
                this.claims.put(key, value);
            } else {
                this.claims = new HashMap<>();
            }
            return this;
        }

        /**
         * Adds claims to parameters.
         *
         * @param claims a map of claims
         * @return TokenParametersBuilder
         */
        public TokenParametersBuilder claims(
                final Map<String, Object> claims
        ) {
            if (this.claims != null) {
                this.claims.putAll(claims);
            } else {
                this.claims = new HashMap<>();
            }
            return this;
        }

        /**
         * Sets issued date for JWT token.
         *
         * @param issuedAt date of issuing
         * @return TokenParametersBuilder
         */
        public TokenParametersBuilder issuedAt(
                final Date issuedAt
        ) {
            this.issuedAt = issuedAt;
            return this;
        }

        /**
         * Sets expiration date for JWT token.
         *
         * @param expiredAt date of expiration
         * @return TokenParametersBuilder
         */
        public TokenParametersBuilder expiredAt(
                final Date expiredAt
        ) {
            this.expiredAt = expiredAt;
            return this;
        }

        /**
         * Sets subject to parameters.
         *
         * @param subject subject of JWT token
         * @return TokenParametersBuilder
         */
        public TokenParametersBuilder subject(
                final String subject
        ) {
            this.subject = subject;
            return this;
        }

        /**
         * Builds final object.
         *
         * @return io.github.ilyalisov.jwt.TokenParameters object
         */
        public TokenParameters build() {
            return new TokenParameters(
                    claims,
                    subject,
                    issuedAt,
                    expiredAt
            );
        }

    }

}
