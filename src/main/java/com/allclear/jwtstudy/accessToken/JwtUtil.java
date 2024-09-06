package com.allclear.jwtstudy.accessToken;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;


@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;
    private final long VALID_TIME = (60*1000)*30;

    /**
     * Access Token을 생성합니다.
     * @param username
     * @return
     */
    public String createAccessToken(String username) {

        Date now = new Date();
        Date expiration = new Date(now.getTime() + VALID_TIME);

        return Jwts.builder()
                .claim("username", username)
                .issuedAt(now)
                .expiration(expiration)
                .signWith(getSecretKey())
                .compact();

    }

    /**
     * Secret key에 알고리즘을 적용해 SecretKey 객체를 생성합니다.
     * @return SecretKey
     */
    public SecretKey getSecretKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
