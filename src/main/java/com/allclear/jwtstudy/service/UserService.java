package com.allclear.jwtstudy.service;

import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import com.allclear.jwtstudy.dto.UserJoinRequest;
import com.allclear.jwtstudy.dto.UserLoginRequest;
import com.allclear.jwtstudy.entity.User;
import com.allclear.jwtstudy.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserAuth userAuth;

    public void singUp(UserJoinRequest userJoinRequest) {
        User user = userCheck(userJoinRequest.getUsername());
        if(user != null) {
            throw new RuntimeException("이미 가입된 계정입니다.");
        }

        User saveUser = User.builder()
                .username(userJoinRequest.getUsername())
                .password(userJoinRequest.getPassword())
                .email(userJoinRequest.getEmail())
                .build();

        userRepository.save(saveUser);
    }

    public HttpHeaders singIn(UserLoginRequest userRequest) {

        User user = userCheck(userRequest.getUsername());
        if(user == null) {
            throw new RuntimeException("가입된 계정이 없습니다.");
        }

        return userAuth.creatHeaders(userRequest.getUsername());
    }

    private User userCheck(String username) {
        return userRepository.findByUsername(username);

    }

}
