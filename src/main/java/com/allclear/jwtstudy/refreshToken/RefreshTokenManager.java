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

    /**
     * 생성한 Refresh Token을 Redis Cache에 저장합니다.
     * @return String refreshTokenKey
     */
    public String saveRefreshToken() {
        String refreshToken = createRefreshToken();
        String key = refreshTokenCheck();
        redisTemplate.opsForValue().set(key, refreshToken);

        return key;
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
     * key에 해당하는 RefreshToken이 존재하면 다시 난수를 생성하는 작업을 진행합니다.
     *  - stackOverFlowError 가능성이 있어서 추후 수정 예정
     * @return String refreshTokenKey
     */
    private String refreshTokenCheck() {
        String key = createSecureRandom(KEY_LEN);
        String refreshToken = getRefreshToken(key);

        if(refreshToken != null) {
            refreshTokenCheck();
        }

        return key;
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
