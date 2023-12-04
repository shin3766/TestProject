package com.example.springtest.dto;


import com.example.springtest.entity.User;
import com.example.springtest.entity.UserRole;

public record JwtUser(
        Long id,
        String username,
        UserRole role
) {

    public static JwtUser of(User user) {
        return new JwtUser(user.getId(), user.getUsername(), user.getRole());
    }
}
