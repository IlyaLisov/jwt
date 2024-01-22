package io.github.ilyalisov.jwt.config.redis;

/**
 * Schema interface for
 * {@link io.github.ilyalisov.jwt.storage.RedisTokenStorageImpl}.
 */
public interface RedisSchema {

    /**
     * Redis key for JWT token to be stored with.
     *
     * @param subject "sub" of JWT token
     * @param type    token type
     * @return Redis key
     */
    String subjectTokenKey(
            String subject,
            String type
    );

}
