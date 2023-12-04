package com.example.springtest.controller;

import com.example.springtest.dto.MessageDto;
import com.example.springtest.exception.NotFoundEntityException;
import jakarta.mail.SendFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionController {

    @ExceptionHandler({SendFailedException.class})
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public MessageDto sendFail() {
        return new MessageDto("이메일 전송에 실패했습니다.");
    }

    @ExceptionHandler(NotFoundEntityException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public MessageDto notFound(NotFoundEntityException ex){
        String message = ex.getMessage();
        if(message == null) message = "존재하지 않습니다.";
        return new MessageDto(message);
    }

    @ExceptionHandler({AccessDeniedException.class})
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public MessageDto notAllowed(AccessDeniedException ex){
        return new MessageDto(ex.getMessage());
    }
}
