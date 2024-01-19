package io.github.ilyalisov.jwt.storage;

import io.github.ilyalisov.jwt.config.TokenParameters;
import io.github.ilyalisov.jwt.config.redis.DefaultRedisSchema;
import io.github.ilyalisov.jwt.config.redis.RedisSchema;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.params.SetParams;

public class RedisTokenStorageImpl implements TokenStorage {

    /**
     * Pool of Redis connections.
     */
    private final JedisPool jedisPool;

    /**
     * Schema of keys for storing JWT tokens.
     */
    private final RedisSchema redisSchema;

    /**
     * Creates object with provided JedisPool and DefaultRedisSchema.
     *
     * @param jedisPool JedisPool object
     */
    public RedisTokenStorageImpl(
            final JedisPool jedisPool
    ) {
        this.jedisPool = jedisPool;
        this.redisSchema = new DefaultRedisSchema();
    }

    /**
     * Creates object with provided JedisPool and RedisSchema.
     *
     * @param jedisPool   JedisPool object
     * @param redisSchema RedisSchema object
     */
    public RedisTokenStorageImpl(
            final JedisPool jedisPool,
            final RedisSchema redisSchema
    ) {
        this.jedisPool = jedisPool;
        this.redisSchema = redisSchema;
    }

    @Override
    public void save(
            final String token,
            final TokenParameters params
    ) {
        try (Jedis jedis = jedisPool.getResource()) {
            String tokenKey = redisSchema.subjectTokenKey(
                    params.getSubject(),
                    (String) params.getClaims().get("type")
            );
            jedis.set(
                    tokenKey,
                    token,
                    SetParams.setParams().pxAt(
                            params.getExpiredAt().getTime()
                    )
            );
        }
    }

    @Override
    public boolean exists(
            final String token,
            final TokenParameters params
    ) {
        try (Jedis jedis = jedisPool.getResource()) {
            String tokenKey = redisSchema.subjectTokenKey(
                    params.getSubject(),
                    (String) params.getClaims().get("type")
            );
            return token.equals(jedis.get(tokenKey));
        }
    }

    @Override
    public String get(
            final TokenParameters params
    ) {
        try (Jedis jedis = jedisPool.getResource()) {
            String tokenKey = redisSchema.subjectTokenKey(
                    params.getSubject(),
                    params.getType()
            );
            return jedis.get(tokenKey);
        }
    }

}
