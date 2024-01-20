package io.github.ilyalisov.jwt.service;

import io.github.ilyalisov.jwt.config.TokenParameters;

import java.util.Map;

/**
 * Interface of TokenService.
 */
public interface TokenService {

    /**
     * Creates JWT token with provided parameters.
     *
     * @param params parameters for JWT token
     * @return JWT token
     */
    String create(
            TokenParameters params
    );

    /**
     * Checks if JWT token is expired by current time.
     *
     * @param token JWT token to be checked
     * @return true - if JWT token expired, false - otherwise
     */
    boolean isExpired(
            String token
    );

    /**
     * Checks if JWT token has a key-value pair in payload.
     *
     * @param token JWT token
     * @param key   key of payload
     * @param value value of payload
     * @return true - if JWT token has a provided key-value pair in payload,
     * false - otherwise
     */
    boolean has(
            String token,
            String key,
            Object value
    );

    /**
     * Returns "sub" of JWT token.
     *
     * @param token JWT token
     * @return "sub" of JWT token
     */
    String getSubject(
            String token
    );

    /**
     * Returns type of JWT token.
     *
     * @param token JWT token
     * @return type of JWT token
     */
    String getType(
            String token
    );

    /**
     * Returns payload of JWT token as a Map.
     *
     * @param token JWT token
     * @return a map of key-value pairs from payload
     */
    Map<String, Object> claims(
            String token
    );

}
