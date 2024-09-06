package com.allclear.jwtstudy.refreshToken;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.allclear.jwtstudy.accessToken.JwtUtil;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private final long VALID_TIME = (60*1000)*60*24*7;
    private final int KEY_LEN = 100000000;

    public String saveRefreshToken() {
        String refreshToken = createRefreshToken();
        String key = refreshTokenCheck();
        redisTemplate.opsForValue().set(key, refreshToken);

        return key;
    }

    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    private String refreshTokenCheck() {
        String key = createSecureRandom(KEY_LEN);
        String refreshToken = getRefreshToken(key);

        if(refreshToken != null) {
            refreshTokenCheck();
        }

        return key;
    }

    public String createRefreshToken() {
        String payload = createSecureRandom(1000000);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + VALID_TIME);

        return Jwts.builder()
                .claim("refresh", payload)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(jwtUtil.getSecretKey())
                .compact();

    }

    private String createSecureRandom(int limit) {
        SecureRandom secureRandom = new SecureRandom();
        return String.valueOf(secureRandom.nextInt(limit));

    }
}
