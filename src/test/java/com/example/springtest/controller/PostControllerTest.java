package com.example.springtest.controller;

import com.example.springtest.IntegrationTest;
import com.example.springtest.dto.postDto.PostRequestDto;
import com.example.springtest.entity.Post;
import com.example.springtest.entity.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.transaction.annotation.Transactional;

import static com.example.springtest.entity.UserRole.USER;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.securityContext;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@DisplayName("게시글 API 통합 테스트")
class PostControllerTest extends IntegrationTest {

    User user;
    Post post;

    @BeforeEach
    void init() {
        user = saveUser("한정석", "1234", "test@gmail.com", USER);
    }

    @DisplayName("게시글 생성 성공")
    @Test
    void createPostSuccess() throws Exception {
        //given
        SecurityContext context = contextJwtUser(user.getId(), user.getUsername(), user.getRole());
        var title = "제목입니다";
        var content = "내용입니다";
        var request = new PostRequestDto(title, content);
        //when //then
        mockMvc.perform(post("/api/v1/post")
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(request))
                .with(securityContext(context))
        ).andDo(print()
        ).andExpectAll(
                status().isOk(),
                jsonPath("$.id").exists(),
                jsonPath("$.title").value(title),
                jsonPath("$.content").value(content),
                jsonPath("$.author").value(user.getUsername()),
                jsonPath("$.createdAt").exists(),
                jsonPath("$.activatedAt").exists()
        );
    }

    @DisplayName("로그인되지 않은 유저 게시글 생성 실패")
    @Test
    void createPostFailWhenNotLoginUser() throws Exception {
        //given
        var request = new PostRequestDto("테스트 제목", "테스트 내용");
        //when //then
        mockMvc.perform(post("/api/v1/post")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.message").value("권한이 없습니다.")
                );
    }

    @DisplayName("선택 게시글 조회 성공")
    @Test
    void readPostSuccess() throws Exception {
        //given
        SecurityContext context = contextJwtUser(user.getId(), user.getUsername(), user.getRole());
        post = savePost("test post", "test content", user);
        //when //then
        mockMvc.perform(get("/api/v1/post/" + post.getId())
                        .with(securityContext(context)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(post.getId()),
                        jsonPath("$.author").value(user.getUsername()),
                        jsonPath("$.title").value(post.getTitle()),
                        jsonPath("$.content").value(post.getContent()),
                        jsonPath("$.createdAt").exists()
                );
    }

    @DisplayName("선택 게시글 수정 성공")
    @Test
    void updatePostSuccess() throws Exception {
        //given
        post = savePost("test post", "test content", user);
        var request = new PostRequestDto("new test post", "new test content");
        SecurityContext context = contextJwtUser(user.getId(), user.getUsername(), user.getRole());
        //then //when
        mockMvc.perform(patch("/api/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(securityContext(context)))
                .andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.id").value(post.getId()),
                        jsonPath("$.author").value(user.getUsername()),
                        jsonPath("$.title").value(post.getTitle()),
                        jsonPath("$.content").value(post.getContent()),
                        jsonPath("$.createdAt").exists(),
                        jsonPath("$.activatedAt").exists()
                );
    }

    @DisplayName("로그인되지 않은 유저 게시글 수정 실패")
    @Test
    void updatePostFailWhenNotLoginUser() throws Exception {
        //given
        post = savePost("test post", "test content", user);
        var request = new PostRequestDto("new test post", "new test content");
        //when //then
        mockMvc.perform(patch("/api/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                ).andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.message").value("권한이 없습니다.")
                );
    }

    @DisplayName("작성한 유저가 아닌 다른 유저가 게시글 수정 실패")
    @Test
    void updatePostFailWhenUserIsNotAuthor() throws Exception {
        //given
        User author = saveUser("일이삼", "1234", "test@test.com", USER);
        post = savePost("test post", "test content", author);
        var request = new PostRequestDto("not Author title", "not Author content");
        SecurityContext context = contextJwtUser(user.getId(), user.getUsername(), user.getRole());
        //when //then
        mockMvc.perform(patch("/api/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request))
                        .with(securityContext(context))
                ).andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.message").value("권한이 없습니다.")
                );
    }

    @DisplayName("선택 게시글 삭제 성공")
    @Test
    void deletePostSuccess() throws Exception {
        //given
        post = savePost("test post", "test content", user);
        SecurityContext context = contextJwtUser(user.getId(), user.getUsername(), user.getRole());
        //when //then
        mockMvc.perform(delete("/api/v1/post/" + post.getId())
                        .with(securityContext(context))
                ).andDo(print())
                .andExpectAll(
                        status().isOk(),
                        jsonPath("$.message").value("선택 게시글이 삭제됐습니다.")
                );
    }

    @DisplayName("로그인되지 않은 유저 게시글 삭제 실패")
    @Test
    void deletePostFailWhenNotLoginUser() throws Exception {
        //given
        post = savePost("test post", "test content", user);
        //when //then
        mockMvc.perform(delete("/api/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                ).andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.message").value("권한이 없습니다.")
                );
    }

    @DisplayName("작성한 유저가 아닌 다른 유저가 게시글 삭제 실패")
    @Test
    void deletePostFailWhenUserIsNotAuthor() throws Exception {
        //given
        User author = saveUser("일이삼", "1234", "test@test.com", USER);
        post = savePost("test post", "test content", author);
        SecurityContext context = contextJwtUser(user.getId(), user.getUsername(), user.getRole());
        //when //then
        mockMvc.perform(delete("/api/v1/post/" + post.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(securityContext(context))
                ).andDo(print())
                .andExpectAll(
                        status().isForbidden(),
                        jsonPath("$.message").value("권한이 없습니다.")
                );
    }
}