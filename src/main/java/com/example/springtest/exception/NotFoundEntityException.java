package com.example.springtest.exception;

/**
 * Repository에서 데이터를 조회하지 못했을 때, 발생하는 예외 케이스
 */
public class NotFoundEntityException extends RuntimeException{
    public NotFoundEntityException() {
    }

    public NotFoundEntityException(String message) {
        super(message);
    }
}
