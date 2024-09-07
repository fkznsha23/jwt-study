package com.allclear.jwtstudy.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.allclear.jwtstudy.refreshToken.RefreshTokenManager;
import com.allclear.jwtstudy.service.UserAuth;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/token")
public class TokenController {

    private final RefreshTokenManager refreshTokenManager;
    private final UserAuth userAuth;

    /**
     * Access token이 만료되었을 때 refresh token을 발급받습니다.
     * @param username
     * @return ResponseEntity<String>
     */
    @GetMapping("/refresh")
    public ResponseEntity<String> newToken(@RequestHeader("RefreshToken") String username) {
        log.info("username = {}", username);
        HttpHeaders headers = null;

        try {
            refreshTokenManager.refreshTokenCheck(username);

        } catch (RuntimeException ex) {
            headers = userAuth.creatHeaders(username);

        }

        return ResponseEntity.status(HttpStatus.OK).headers(headers).body("새로운 access token이 발급되었습니다");
    }
}
