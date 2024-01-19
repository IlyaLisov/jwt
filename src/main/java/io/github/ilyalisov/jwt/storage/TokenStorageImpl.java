package io.github.ilyalisov.jwt.storage;

import io.github.ilyalisov.jwt.config.TokenParameters;

import java.util.HashMap;
import java.util.Map;

//TODO invalidate tokens - set expiration time
public class TokenStorageImpl implements TokenStorage {

    /**
     * Inner map of key-value pairs.
     */
    private final Map<String, String> tokens;

    /**
     * Creates an object.
     */
    public TokenStorageImpl(
    ) {
        this.tokens = new HashMap<>();
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

}
