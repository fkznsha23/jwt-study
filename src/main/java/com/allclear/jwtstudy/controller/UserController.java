package com.allclear.jwtstudy.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.allclear.jwtstudy.dto.UserDetailsImpl;
import com.allclear.jwtstudy.dto.UserJoinRequest;
import com.allclear.jwtstudy.dto.UserLoginRequest;
import com.allclear.jwtstudy.service.UserService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    @PostMapping("/join")
    public ResponseEntity<String> joinUser(@Validated @RequestBody UserJoinRequest userJoinRequest) {
        userService.singUp(userJoinRequest);

        return ResponseEntity.ok("회원가입이 완료되었습니다.");
    }

    @PostMapping("/login")
    public ResponseEntity<String> loginUser(@Validated @RequestBody UserLoginRequest userRequest) {
        HttpHeaders httpHeaders = userService.singIn(userRequest);

        return ResponseEntity.status(HttpStatus.OK).headers(httpHeaders).body("로그인이 완료되었습니다.");
    }

    @GetMapping("/{no}")
    public ResponseEntity<String> getUser(@AuthenticationPrincipal UserDetailsImpl userDetails, @PathVariable int no) {
        log.info("username = {}, password = {}, no = {}", userDetails.getUsername(), userDetails.getPassword(), no);

        return ResponseEntity.ok("성공!");
    }


}
