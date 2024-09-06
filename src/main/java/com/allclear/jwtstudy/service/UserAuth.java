package com.allclear.jwtstudy.service;

import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import com.allclear.jwtstudy.accessToken.JwtUtil;
import com.allclear.jwtstudy.refreshToken.RefreshTokenManager;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class UserAuth {

    private final JwtUtil jwtUtil;
    private final RefreshTokenManager refreshTokenManager;

    /**
     * Access Token과 Refresh Token이 저장된 Headers를 생성합니다.
     * @param username
     * @return HttpHeaders httpHeaders
     */
    public HttpHeaders creatHeaders(String username) {
        HttpHeaders httpHeaders = new HttpHeaders();
        String accessToken = "Bearer " + jwtUtil.createAccessToken(username);
        String refreshTokenId = refreshTokenManager.saveRefreshToken();
        httpHeaders.set("Authorization", accessToken);
        httpHeaders.set("RefreshtToken", refreshTokenId);

        return httpHeaders;
    }

}
