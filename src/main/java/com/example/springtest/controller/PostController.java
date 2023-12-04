package com.example.springtest.controller;

import com.example.springtest.dto.MessageDto;
import com.example.springtest.dto.postDto.PostRequestDto;
import com.example.springtest.dto.postDto.PostResponseDto;
import com.example.springtest.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

// todo all
@RestController
@RequestMapping("/api/v1/post")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    // CREATE: 게시물 작성
    @PostMapping
    public PostResponseDto createPost(@RequestBody PostRequestDto requestDto) {
        return postService.createPost(requestDto);
    }

    // READ: 게시물 선택 조회
    @GetMapping("/{id}")
    public PostResponseDto getPost(@PathVariable Long id) {
        return postService.getPost(id);
    }


    // UPDATE: 선택 게시물 업데이트
    @PatchMapping("/{id}")
    public PostResponseDto updatePost(@PathVariable Long id, @RequestBody PostRequestDto requestDto) {
        return postService.updatePost(id, requestDto);
    }

    // DELETE: 선택 게시물 삭제
    @DeleteMapping("/{id}")
    public MessageDto deletePost(@PathVariable Long id) {
        String resultMessage = postService.deletePost(id);
        return new MessageDto(resultMessage);
    }
}

