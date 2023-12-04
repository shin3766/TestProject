package com.example.springtest.dto;


import com.example.springtest.entity.Comment;

import java.time.LocalDateTime;

public record CommentDto(
        Long id,
        String content,
        String author,
        LocalDateTime createdAt
) {
    public static CommentDto of(Comment c) {
        return new CommentDto(c.getId(), c.getContent(), c.getAuthor(), c.getCreatedAt());
    }
}
