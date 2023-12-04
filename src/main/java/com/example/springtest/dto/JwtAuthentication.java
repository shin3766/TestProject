package com.example.springtest.dto;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * jwt 토큰 기반 인증 로직을 구현할 때 사용하는 커스텀 Authentication 클래스입니다.
 */
public class JwtAuthentication extends AbstractAuthenticationToken {

    private final JwtUser user;

    public JwtAuthentication(JwtUser jwtUser, Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        setAuthenticated(true);
        this.user = jwtUser;
    }

    @Override
    public Object getCredentials() {
        return "";
    }

    @Override
    public Object getPrincipal() {
        return user;
    }
}
