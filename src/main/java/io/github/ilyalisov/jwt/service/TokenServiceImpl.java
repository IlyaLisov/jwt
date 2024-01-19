package io.github.ilyalisov.jwt.service;

import io.github.ilyalisov.jwt.config.TokenParameters;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Basic implementation of TokenService.
 */
public class TokenServiceImpl implements TokenService {

    /**
     * Secret key for verifying JWT token.
     */
    private final SecretKey key;

    /**
     * Name of field in JWT token for its type.
     */
    public static final String TOKEN_TYPE_KEY = "tokenType";

    /**
     * Creates an object.
     *
     * @param secret secret of key for JWT token generation
     */
    public TokenServiceImpl(
            final String secret
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    @Override
    public String create(
            final TokenParameters params
    ) {
        Claims claims = Jwts.claims()
                .subject(params.getSubject())
                .add(params.getClaims())
                .add(TOKEN_TYPE_KEY, params.getType())
                .build();
        return Jwts.builder()
                .claims(claims)
                .issuedAt(params.getIssuedAt())
                .expiration(params.getExpiredAt())
                .signWith(key)
                .compact();
    }

    @Override
    public boolean isExpired(
            final String token
    ) {
        try {
            Jws<Claims> claims = Jwts
                    .parser()
                    .verifyWith(key)
                    .build()
                    .parseSignedClaims(token);
            return claims.getPayload()
                    .getExpiration()
                    .before(new Date());
        } catch (ExpiredJwtException e) {
            return true;
        }
    }

    @Override
    public boolean has(
            final String token,
            final String key,
            final Object value
    ) {
        Jws<Claims> claims = Jwts
                .parser()
                .verifyWith(this.key)
                .build()
                .parseSignedClaims(token);
        return claims.getPayload()
                .get(key)
                .equals(value);
    }

    @Override
    public String getSubject(
            final String token
    ) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject();
    }

    @Override
    public String getType(
            final String token
    ) {
        return Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .get(TOKEN_TYPE_KEY, String.class);
    }

    @Override
    public Map<String, Object> claims(
            final String token
    ) {
        Jws<Claims> claims = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return new HashMap<>(claims.getPayload());
    }

}
