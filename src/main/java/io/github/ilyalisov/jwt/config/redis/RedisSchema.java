package io.github.ilyalisov.jwt.config.redis;

public interface RedisSchema {

    /**
     * Redis key for JWT token to be stored with.
     *
     * @param subject "sub" of JWT token
     * @param type    token type
     * @return key to store JWT token with
     */
    String subjectTokenKey(
            String subject,
            String type
    );

}
