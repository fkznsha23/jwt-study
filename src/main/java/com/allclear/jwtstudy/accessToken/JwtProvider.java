package com.allclear.jwtstudy.accessToken;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProvider {

    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Value("${SECRET_KEY}")
    private String SECRET_KEY;
    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer ";

    /**
     * api 요청 헤더에서 Access Token을 추출합니다.
     * - 추출한 Access Token에서 Bearea을 제외합니다.
     * @param request
     * @return String AccessToken
     */
    public String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION);
        log.info("token = {}", bearerToken);

        if(bearerToken != null && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }

        return null;

    }

    /**
     * 인증 정보를 생성합니다.
     * @param token
     * @return Authentication auth
     */
    public Authentication getAuthentication(String token) {
        String username = decodeUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.info("username = {}, password = {}", userDetails.getUsername(), userDetails.getPassword());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    /**
     * Payload에서 username을 추출합니다.
     * @param token
     * @return String username
     */
    public String decodeUsername(String token) {

        return String.valueOf(getClaims(token).get("username"));
    }

    /**
     * Access Token에서 Payload를 추출합니다.
     * @param token
     * @return Claims claims
     */
    private Claims getClaims(String token) {
        Claims claims = null;
        try {
            claims = Jwts.parser()
                    .verifyWith(jwtUtil.getSecretKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

        } catch (ExpiredJwtException e) {
            throw new RuntimeException("만료된 토큰입니다.");
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 JWT 토큰입니다.");
        }

        log.info("Claims = {}", claims);
        return claims;
    }
}
