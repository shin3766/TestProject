package com.example.springtest.controller;

import com.example.newsfeedproject.dto.PageDto;
import com.example.newsfeedproject.dto.PostSearchConditionParam;
import com.example.newsfeedproject.service.PostService;
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
