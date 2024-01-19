package io.github.ilyalisov.jwt.fake;

import io.github.ilyalisov.jwt.config.TokenParameters;
import io.github.ilyalisov.jwt.storage.TokenStorage;

import java.util.HashMap;
import java.util.Map;

public class FakeTokenStorageImpl implements TokenStorage {

    private final Map<String, String> tokens = new HashMap<>();

    private String subjectTokenKey(
            final String subject,
            final String type
    ) {
        return "tokens:" + subject + ":" + type;
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

}
