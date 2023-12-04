package com.example.springtest.jwt;

import com.example.springtest.dto.JwtUser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.Optional;

@Component
@Slf4j
public class JwtUtil {
    // Header KEY 값
    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String REFRESH_AUTHORIZATION_HEADER = "Refresh-Authorization";
    // Token 식별자
    public static final String BEARER_PREFIX = "Bearer ";
    public static final String REFRESH_TYPE = "REFRESH";
    public static final String ACCESS_TYPE = "ACCESS";

    private final long accessTokenTime;
    private final long refreshTokenTime;

    private final Key key;
    private final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    public JwtUtil(
            @Value("${jwt.accessToken.duration}")
            long accessTokenTime,
            @Value("${jwt.refreshToken.duration}")
            long refreshTokenTime,
            @Value("${jwt.secret.key}")
            String secretKey
    ) {
        this.accessTokenTime = accessTokenTime;
        this.refreshTokenTime = refreshTokenTime;
        byte[] bytes = Base64.getDecoder().decode(secretKey);
        key = Keys.hmacShaKeyFor(bytes);
    }

    // 토큰 생성
    public String createToken(JwtUser user, String type) {
        long duration = 0;

        if (type.equals(REFRESH_TYPE)) {
            duration = refreshTokenTime;
        } else if (type.equals(ACCESS_TYPE)) {
            duration = accessTokenTime;
        }

        try {
            return BEARER_PREFIX + Jwts.builder()
                    .setSubject(user.id().toString())
                    .claim("type", type)
                    .claim("user", objectMapper.writeValueAsString(user))
                    .setExpiration(new Date(new Date().getTime() + duration)) // 만료 시간
                    .setIssuedAt(new Date()) // 발급일
                    .signWith(key, signatureAlgorithm) // 암호화 알고리즘
                    .compact();
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    private String removePrefix(String tokenValue) {
        if (StringUtils.hasText(tokenValue) && tokenValue.startsWith(BEARER_PREFIX)) {
            return tokenValue.substring(7);
        }
        log.error("Not Found Token");
        throw new NullPointerException("Not Found Token");
    }

    private boolean validateToken(String token) {
        try {
            Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
            return true;
        } catch (RuntimeException e) {
            return false;
        }
    }

    private Claims getCustomerClaim(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * 토큰에 담긴 유저 정보를 매핑하는 함수입니다.
     * 토큰이 유효하지 않거나 로직에서 원하는 타입이 아닌 경우에는 empty를 반환합니다.
     *
     * @param token Bearer jwtToken...
     * @param type  refresh, access 토큰 타입
     * @return 유저 정보가 담긴 Optinal 객체
     */
    public Optional<JwtUser> getJwtUser(String token, String type)  {
        try {
            if (token == null || !token.startsWith("Bearer")) return Optional.empty();

            token = removePrefix(token);
            if (!validateToken(token)) return Optional.empty();

            Claims jwt = getCustomerClaim(token);
            var jwtUser = objectMapper.readValue(jwt.get("user", String.class), JwtUser.class);

            String tokenType = jwt.get("type", String.class);
            if (!tokenType.equals(type)) return Optional.empty();

            return Optional.of(jwtUser);

        } catch (JsonProcessingException ex){
            return Optional.empty();
        }
    }
}