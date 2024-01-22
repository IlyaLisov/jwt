package io.github.ilyalisov.jwt.storage;

import io.github.ilyalisov.jwt.config.TokenParameters;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Basic implementation of TokenStorage.
 */
public class TokenStorageImpl implements TokenStorage {

    /**
     * Inner map of key-value pairs.
     */
    private final ConcurrentHashMap<String, String> tokens;

    /**
     * Creates an object.
     */
    public TokenStorageImpl(
    ) {
        this.tokens = new ConcurrentHashMap<>();
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
        //TODO invalidate tokens - set expiration time
        tokens.put(tokenKey, token);
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
        return token.equals(tokens.get(tokenKey));
    }

    @Override
    public String get(
            final TokenParameters params
    ) {
        String tokenKey = subjectTokenKey(
                params.getSubject(),
                params.getType()
        );
        return tokens.get(tokenKey);
    }

    @Override
    public boolean remove(
            final String token
    ) {
        boolean deleted = false;
        for (Map.Entry<String, String> entry : tokens.entrySet()) {
            if (entry.getValue().equals(token)) {
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

}
