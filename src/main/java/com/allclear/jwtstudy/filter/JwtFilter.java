package com.allclear.jwtstudy.filter;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import com.allclear.jwtstudy.accessToken.JwtProvider;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    /**
     * api 요청이 들어올 때 실행될 메소드입니다.
     * @param request
     * @param response
     * @param filterChain
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        log.info("api = {}", request.getRequestURI());

        String noBearerToken = jwtProvider.extractToken(request);

        log.info("No bearer token = {}", noBearerToken);

        if(noBearerToken != null) {
            Authentication authentication = jwtProvider.getAuthentication(noBearerToken);
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("authentication = {}", authentication);
        }

        filterChain.doFilter(request, response);
    }

}
