package com.example.springtest.service;

import com.example.springtest.dto.JwtUser;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 로그인 유저의 상태 관리 서비스를 제공합니다.
 * 1. JWT 기반 로그인 유저 정보 조회
 * 2. 이메일 인증 코드 관리 서비스
 */
@Component
public class UserStatusService {

    private final ConcurrentMap<String, String> redis = new ConcurrentHashMap<>();

    /**
     * JwtAuthorizationFilter에서 인가된 jwt 토큰 정보를 SecurityContext에서 조회한다.
     *
     * @return jwt 토큰에 담긴 유저 정보 dto를 반환
     */
    public JwtUser getLoginUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth.getPrincipal() instanceof JwtUser) return (JwtUser) auth.getPrincipal();
        throw new AccessDeniedException("권한이 없습니다.");
    }

    public void saveEmailAuthCode(String email, String code) {
        redis.put(email, code);
    }

    public void removeEmailAuthCode(String email){
        redis.remove(email);
    }

    public boolean matchesEmailAuthCode(String email, String code) {
        if (!redis.containsKey(email)) return false;
        return redis.get(email).equals(code);
    }
}
