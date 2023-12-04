package com.example.springtest.entity;

import com.example.springtest.dto.postDto.PostRequestDto;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Post {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String content;
    @CreatedDate
    private LocalDateTime createdAt;
    @LastModifiedDate
    private LocalDateTime activatedAt;
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    @Builder
    private Post(String title, String content, User user) {
        this.title = title;
        this.content = content;
        this.user  = user;
    }

    public static Post foreign(Long id) {
        Post post = new Post();
        post.id = id;
        return post;
    }

    public Post(PostRequestDto requestDto) {
        this.title = requestDto.title();
        this.content = requestDto.content();
        this.createdAt = LocalDateTime.now();
    }
    public void update(PostRequestDto requestDto) {
        this.title = requestDto.title();
        this.content = requestDto.content();
    }
}
