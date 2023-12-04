package com.example.springtest.config;


import com.example.newsfeedproject.dto.MessageDto;
import com.example.newsfeedproject.jwt.JwtAuthorizationFilter;
import com.example.newsfeedproject.jwt.JwtUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final JwtUtil jwtUtil;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Bean
    public JwtAuthorizationFilter jwtAuthorizationFilter() {
        return new JwtAuthorizationFilter(jwtUtil);
    }

    // 기본 UserDetailsService 비활성화
    @Bean
    public UserDetailsService userDetailService() {
        return username -> null;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        // CSRF 설정
        http.csrf((csrf) -> csrf.disable());

        // 기본 설정인 Session 방식은 사용하지 않고 JWT 방식을 사용하기 위한 설정
        http.sessionManagement((sessionManagement) ->
                sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
        );

        http.authorizeHttpRequests((authorizeHttpRequests) ->
                authorizeHttpRequests
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll() // resources 접근 허용 설정
                        .requestMatchers(
                                antMatcher(POST, "/api/v1/login"),
                                antMatcher(POST, "/api/v1/signup"),
                                antMatcher(POST, "/api/v1/signup/email"),
                                antMatcher(GET, "/api/v1/posts"),
                                antMatcher(GET, "/api/v1/post/**"),
                                antMatcher(GET, "/api/v1/refresh**"),
                                antMatcher(GET, "/api/v1/comment/**")
                        ).permitAll()
                        .anyRequest().authenticated()
        );

        http.exceptionHandling(config -> {
                    config.authenticationEntryPoint(errorPoint());
                    config.accessDeniedHandler(accessDeniedHandler());
                }
        );
        // 필터 관리
        http.addFilterBefore(jwtAuthorizationFilter(), UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, ex) -> {
            var message = new MessageDto("권한이 없습니다.");
            String body = objectMapper.writeValueAsString(message);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(body);
        };
    }

    private AuthenticationEntryPoint errorPoint() {
        return (request, response, authException) -> {
            var message = new MessageDto("권한이 없습니다.");
            String body = objectMapper.writeValueAsString(message);
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType(MediaType.APPLICATION_JSON_VALUE);
            response.getWriter().write(body);
        };

    }
}