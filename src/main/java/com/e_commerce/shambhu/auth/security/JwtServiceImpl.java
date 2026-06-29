package com.e_commerce.shambhu.auth.security;

import com.e_commerce.shambhu.auth.config.JwtProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import org.springframework.security.core.GrantedAuthority;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class JwtServiceImpl implements JwtService {

    private final JwtProperties jwtProperties;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;
    }

    @Override
    public String generateAccessToken(UserDetails userDetails) {

        CustomUserDetails customUser =
                (CustomUserDetails) userDetails;

        List<String> roles =
                customUser.getAuthorities()
                        .stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList();

        Map<String, Object> claims =
                new HashMap<>();

        claims.put("userId", customUser.getId());

        claims.put("roles", roles);

        return generateAccessToken(
                claims,
                customUser
        );
    }

    @Override
    public String generateAccessToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails) {

        Date issuedAt = new Date();

        Date expiration =
                new Date(
                        issuedAt.getTime()
                                + jwtProperties.getAccessTokenExpiration()
                );

        return Jwts.builder()
                .claims(extraClaims)
                .subject(userDetails.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(
                        getSigningKey()
                )
                .compact();
    }

    @Override
    public String generateRefreshToken(UserDetails userDetails) {

        Date issuedAt = new Date();

        Date expiration = new Date(
                issuedAt.getTime()
                        + jwtProperties.getRefreshTokenExpiration()
        );

        return Jwts.builder()
                .subject(userDetails.getUsername())
                .issuer(jwtProperties.getIssuer())
                .issuedAt(issuedAt)
                .expiration(expiration)
                .signWith(
                        getSigningKey()
                )
                .compact();
    }

    @Override
    public String extractUsername(String token) {

        return extractAllClaims(token)
                .getSubject();
    }

    @Override
    public Claims extractAllClaims(String token) {

        return Jwts
                .parser()
                .verifyWith((SecretKey) getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    @Override
    public boolean isTokenValid(
            String token,
            UserDetails userDetails) {

        String username =
                extractUsername(token);

        return username.equals(userDetails.getUsername())
                && !isTokenExpired(token);
    }

    @Override
    public boolean isTokenExpired(String token) {

        return extractExpiration(token)
                .before(new Date());
    }

    private Key getSigningKey() {

        byte[] keyBytes = Decoders.BASE64.decode(
                jwtProperties.getSecret()
        );

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Date extractExpiration(String token) {

        return extractAllClaims(token)
                .getExpiration();
    }
}