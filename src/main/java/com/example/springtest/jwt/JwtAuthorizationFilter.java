package com.example.springtest.jwt;

import com.example.springtest.dto.JwtAuthentication;
import com.example.springtest.dto.JwtUser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import static com.example.springtest.jwt.JwtUtil.ACCESS_TYPE;
import static com.example.springtest.jwt.JwtUtil.AUTHORIZATION_HEADER;


@Slf4j(topic = "JWT 검증 및 인가")
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    public JwtAuthorizationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain filterChain) throws ServletException, IOException {

        String token = req.getHeader(AUTHORIZATION_HEADER);

        Optional<JwtUser> bearerToken = jwtUtil.getJwtUser(token, ACCESS_TYPE);

        // 유효한 엑세스 토큰인 경우 인가 처리
        bearerToken.ifPresent(this::setAuthentication);
        filterChain.doFilter(req, res);
    }

    public void setAuthentication(JwtUser user) {
        var authorities = List.of(new SimpleGrantedAuthority(user.role().name()));
        var authentication = new JwtAuthentication(user, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}