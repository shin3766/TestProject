package com.example.springtest.dto;

public record PostSearchConditionParam(
        int size,
        int page,
        String keyword
) {
    public int offset() {
        if (page == 0) return 0;
        return page * size;
    }
}
