package com.example.springtest.controller;


import com.example.springtest.dto.CreateEmailCodeRequest;
import com.example.springtest.dto.JwtUser;
import com.example.springtest.dto.MessageDto;
import com.example.springtest.jwt.JwtUtil;
import com.example.springtest.service.MailService;
import com.example.springtest.service.UserService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

import static com.example.springtest.jwt.JwtUtil.*;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AuthController {

    private final MailService mailService;
    private final JwtUtil jwtUtil;
    private final UserService userService;

    @Value("${email.auth.subject}")
    private String EMAIL_AUTH_SUBJECT;

    @PostMapping("/signup/email")
    public ResponseEntity<?> requestEmailCode(@RequestBody CreateEmailCodeRequest request) {
        try {
            String code = mailService.createEmailAuthCode();
            mailService.sendEmailAuthCode(request.email(), EMAIL_AUTH_SUBJECT, code);
            return ResponseEntity.ok(new MessageDto(request.email() + "을 확인하세요."));
        } catch (RuntimeException | MessagingException ex) {
            return ResponseEntity.badRequest().body(new MessageDto("입력을 확인해주세요."));
        }
    }

    @DeleteMapping("/signout")
    public ResponseEntity<?> signOut() {
        userService.signOut();
        return ResponseEntity.ok(new MessageDto("탈퇴했습니다."));
    }

    @GetMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestHeader(value = AUTHORIZATION_HEADER, required = false) String token) {
        Optional<JwtUser> bearerToken = jwtUtil.getJwtUser(token, REFRESH_TYPE);
        if (bearerToken.isEmpty())
            return ResponseEntity.badRequest().body(new MessageDto("토큰이 유효하지 않습니다."));

        JwtUser user = bearerToken.get();

        String accessToken = jwtUtil.createToken(user, ACCESS_TYPE);
        String refreshToken = jwtUtil.createToken(user, REFRESH_TYPE);

        return ResponseEntity.ok()
                .header(AUTHORIZATION_HEADER, accessToken)
                .header(REFRESH_AUTHORIZATION_HEADER, refreshToken)
                .build();
    }
}
