package io.github.ilyalisov.jwt.storage;

import io.github.ilyalisov.jwt.config.TokenParameters;
import lombok.Getter;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Basic implementation of TokenStorage.
 */
public class TokenStorageImpl implements TokenStorage {

    /**
     * Inner map of key-value pairs.
     */
    private final ConcurrentHashMap<String, TokenEntry> tokens;

    /**
     * Scheduled executor for cleanup tokens.
     */
    private final ScheduledExecutorService scheduler;

    /**
     * Creates an object.
     */
    public TokenStorageImpl(
    ) {
        this.tokens = new ConcurrentHashMap<>();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(
                () -> {
                    Date date = new Date();
                    for (Map.Entry<
                            String,
                            TokenEntry
                            > entry : tokens.entrySet()) {
                        if (entry.getValue().isAfter(date)) {
                            tokens.remove(entry.getKey());
                        }
                    }
                },
                0,
                1,
                TimeUnit.SECONDS
        );
    }

    private String subjectTokenKey(
            final String subject,
            final String type
    ) {
        return "tokens:" + subject + ":" + type;
    }

    @Override
    public void save(
            final String token,
            final TokenParameters params
    ) {
        String tokenKey = subjectTokenKey(
                params.getSubject(),
                params.getType()
        );
        TokenEntry entry = new TokenEntry(
                token,
                params.getExpiredAt()
        );
        tokens.put(tokenKey, entry);
    }

    @Override
    public boolean exists(
            final String token,
            final TokenParameters params
    ) {
        String tokenKey = subjectTokenKey(
                params.getSubject(),
                params.getType()
        );
        TokenEntry entry = tokens.get(tokenKey);
        if (entry == null) {
            return false;
        }
        return token.equals(entry.token);
    }

    @Override
    public String get(
            final TokenParameters params
    ) {
        String tokenKey = subjectTokenKey(
                params.getSubject(),
                params.getType()
        );
        TokenEntry entry = tokens.get(tokenKey);
        if (entry == null) {
            return null;
        }
        return entry.token;
    }

    @Override
    public boolean remove(
            final String token
    ) {
        boolean deleted = false;
        for (Map.Entry<String, TokenEntry> entry : tokens.entrySet()) {
            if (entry.getValue().token.equals(token)) {
                tokens.remove(entry.getKey());
                deleted = true;
            }
        }
        return deleted;
    }

    @Override
    public boolean remove(
            final TokenParameters params
    ) {
        String tokenKey = subjectTokenKey(
                params.getSubject(),
                params.getType()
        );
        return tokens.remove(tokenKey) != null;
    }

    @Getter
    private static class TokenEntry {

        /**
         * Token.
         */
        private final String token;

        /**
         * Expiration date.
         */
        private final Date expiredAt;

        TokenEntry(
                final String token,
                final Date expiredAt
        ) {
            this.token = token;
            this.expiredAt = expiredAt;
        }

        public boolean isAfter(
                final Date date
        ) {
            return expiredAt.after(date);
        }

    }

}
