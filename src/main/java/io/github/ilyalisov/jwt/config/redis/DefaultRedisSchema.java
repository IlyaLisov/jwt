package io.github.ilyalisov.jwt.config.redis;

/**
 * Default schema for RedisTokenStorageImpl.
 */
public class DefaultRedisSchema implements RedisSchema {

    @Override
    public String subjectTokenKey(
            final String subject,
            final String type
    ) {
        return "tokens:" + subject + ":" + type;
    }

}
