package com.allclear.jwtstudy.refreshToken;

import java.security.SecureRandom;
import java.util.Date;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import com.allclear.jwtstudy.accessToken.JwtUtil;

import io.jsonwebtoken.Jwts;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class RefreshTokenManager {

    private final JwtUtil jwtUtil;
    private final RedisTemplate<String, String> redisTemplate;

    private final long VALID_TIME = (60*1000)*60*24*7;
    private final int KEY_LEN = 100000000;

    /**
     * 생성한 Refresh Token을 Redis Cache에 저장합니다.
     * @return String refreshTokenKey
     */
    public String saveRefreshToken(String username) {
        try {
            String refreshToken = createRefreshToken();
            refreshTokenCheck(username);
            redisTemplate.opsForValue().set(username, refreshToken);

            return username;
        } catch (RuntimeException ex) {
            return null;
        }
    }

    /**
     * key에 해당하는 Refresh Token이 Redis Cache에 저장되어 있는지 확인합니다.
     * @param key
     * @return String refreshToken
     */
    public String getRefreshToken(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    /**
     * key에 해당하는 refresh token이 존재하면 예외를 발생한다.
     * @param username
     */
    public void refreshTokenCheck(String username) {
        String refreshToken = getRefreshToken(username);

        if(refreshToken != null) {
            throw new RuntimeException("이미 refresh token이 존재합니다.");
        }
    }

    /**
     * Refresh Token을 생성합니다.
     * @return String refreshToken
     */
    public String createRefreshToken() {
        String payload = createSecureRandom(1000000);

        Date now = new Date();
        Date expiration = new Date(now.getTime() + VALID_TIME);

        return "Bearer " + Jwts.builder()
                .claim("refresh", payload)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(jwtUtil.getSecretKey())
                .compact();

    }

    /**
     * 난수를 생성합니다.
     * @param limit
     * @return String randomStr
     */
    private String createSecureRandom(int limit) {
        SecureRandom secureRandom = new SecureRandom();
        return String.valueOf(secureRandom.nextInt(limit));

    }

}
