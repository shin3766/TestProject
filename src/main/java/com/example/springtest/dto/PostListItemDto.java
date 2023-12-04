package com.example.springtest.dto;

import java.time.LocalDateTime;

public record PostListItemDto(
       Long id,
       String title,
       LocalDateTime createdAt

) {
}
