package com.example.springtest.dto;

public record CreateCommentRequest(
        Long postId,
        String content
) {
}
