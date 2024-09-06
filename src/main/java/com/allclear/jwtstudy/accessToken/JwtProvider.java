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

    public String extractToken(HttpServletRequest request) {

        String bearerToken = request.getHeader(AUTHORIZATION);
        log.info("token = {}", bearerToken);

        if(bearerToken != null && bearerToken.startsWith(BEARER)) {
            return bearerToken.substring(BEARER.length());
        }

        return null;

    }

    public Authentication getAuthentication(String token) {
        String username = decodeUsername(token);

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        log.info("username = {}, password = {}", userDetails.getUsername(), userDetails.getPassword());

        return new UsernamePasswordAuthenticationToken(userDetails, "", userDetails.getAuthorities());
    }

    public String decodeUsername(String token) {

        return String.valueOf(getClaims(token).get("username"));
    }

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
