package com.example.springtest.controller;

import com.example.springtest.dto.PageDto;
import com.example.springtest.dto.PostSearchConditionParam;
import com.example.springtest.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequestMapping("/api/v1")
@RestController
@RequiredArgsConstructor
public class PostsController {

    private final PostService postService;

    @GetMapping("/posts")
    public ResponseEntity<?> getPostList(@ModelAttribute PostSearchConditionParam conditionParam) {
        PageDto result = postService.getPostList(conditionParam);
        return ResponseEntity.ok(result);
    }
}
