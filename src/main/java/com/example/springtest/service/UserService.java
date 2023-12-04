package com.example.springtest.service;

import com.example.springtest.dto.JwtUser;
import com.example.springtest.dto.LoginRequestDto;
import com.example.springtest.dto.SignupRequestDto;
import com.example.springtest.entity.User;
import com.example.springtest.entity.UserRole;
import com.example.springtest.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final UserStatusService userStatusService;
    private final PasswordEncoder passwordEncoder;

    public JwtUser login(LoginRequestDto loginRequestDto) {
        String username = loginRequestDto.getUsername();
        String password = loginRequestDto.getPassword();

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("등록된 유저가 없습니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        return new JwtUser(user.getId(), user.getUsername(), user.getRole());
    }

    public void signup(SignupRequestDto req) {

        String username = req.username();
        String password = passwordEncoder.encode(req.password());
        String intro = req.intro();
        String email = req.email();
        String code = req.code();

        if (!userStatusService.matchesEmailAuthCode(email, code)) {
            throw new IllegalArgumentException("이메일 코드가 틀립니다.");
        }
        // 회원 중복 확인
        if (userRepository.existsByUsername(username)) {
            throw new IllegalArgumentException("중복된 사용자가 존재합니다.");
        }
        if (userRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("중복된 Email 입니다.");
        }

        userStatusService.removeEmailAuthCode(email);
        UserRole role = UserRole.USER;
        // 사용자 등록
        User user = User.builder()
                .username(username)
                .password(password)
                .email(email)
                .role(role)
                .intro(intro)
                .build();

        userRepository.save(user);
    }

    public void signOut() {
        JwtUser loginUser = userStatusService.getLoginUser();
        userRepository.deleteById(loginUser.id());
    }
}

