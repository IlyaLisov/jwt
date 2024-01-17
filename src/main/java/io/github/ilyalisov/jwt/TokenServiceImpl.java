package io.github.ilyalisov.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TokenServiceImpl implements TokenService {

    /**
     * Secret key for verifying JWT token.
     */
    private final SecretKey key;

    /**
     * Creates io.github.ilyalisov.jwt.TokenServiceImpl object.
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
        Jws<Claims> claims = Jwts
                .parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token);
        return claims.getPayload()
                .getExpiration()
                .before(new Date());
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
