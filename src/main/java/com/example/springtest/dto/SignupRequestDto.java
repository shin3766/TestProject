package com.example.springtest.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.Length;

public record SignupRequestDto(
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-z]).{4,10}$", message = "username은 알파벳 소문자(a~z), 숫자(0~9) 4~10자리로 구성되어야 합니다.")
        String username,
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z]).{8,15}", message = "password는 알파벳 대소문자(a~z, A~Z), 숫자(0~9) 8~15자로 구성되어야 합니다.")
        String password,
        String intro,
        @Email String email,
        @Length(min = 8, max = 8, message = "이메일 인증 코드 8자리를 입력해주세요.")
        String code
) {
}