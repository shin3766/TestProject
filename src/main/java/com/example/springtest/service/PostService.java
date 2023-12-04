package com.example.springtest.service;

import com.example.springtest.dto.JwtUser;
import com.example.springtest.dto.PageDto;
import com.example.springtest.dto.PostSearchConditionParam;
import com.example.springtest.dto.postDto.PostRequestDto;
import com.example.springtest.dto.postDto.PostResponseDto;
import com.example.springtest.entity.Post;
import com.example.springtest.entity.User;
import com.example.springtest.repository.PostDynamicRepository;
import com.example.springtest.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class PostService {

    private final PostRepository postRepository;
    private final PostDynamicRepository postDynamicRepository;
    private final UserStatusService userStatusService;

    public PageDto getPostList(PostSearchConditionParam condition) {
        return postDynamicRepository.findListByCondition(condition);
    }

    public PostResponseDto createPost(PostRequestDto requestDto) {

        JwtUser loginUser = userStatusService.getLoginUser();
        User user = User.foreign(loginUser);

        Post post = Post.builder()
                .content(requestDto.content())
                .user(user)
                .title(requestDto.title())
                .build();

        Post savedPost = postRepository.save(post);

        return new PostResponseDto(savedPost);
    }

    public PostResponseDto getPost(Long id) {
        Post post = postRepository.findFetchJoinUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.") );
        return new PostResponseDto(post);
    }

    @Transactional
    public PostResponseDto updatePost(Long id, PostRequestDto requestDto) {

        Post post = postRepository.findFetchJoinUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.") );

        checkAuthentication(post);
        post.update(requestDto);

        return new PostResponseDto(post);
    }

    public String deletePost(Long id) {

        Post post = postRepository.findFetchJoinUserById(id)
                .orElseThrow(() -> new IllegalArgumentException("선택한 게시글은 존재하지 않습니다.") );

        checkAuthentication(post);
        postRepository.delete(post);
        return "선택 게시글이 삭제됐습니다.";
    }

    private void checkAuthentication(Post post){
        JwtUser loginUser = userStatusService.getLoginUser();
        if(!post.getUser().getId().equals(loginUser.id())){
            throw new AccessDeniedException("권한이 없습니다.");
        }
    }
}
