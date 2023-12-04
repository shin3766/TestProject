package com.example.springtest.dto;

public record UpdateCommentRequest(
        Long id,
        String content
) {
}
