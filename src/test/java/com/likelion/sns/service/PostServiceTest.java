package com.likelion.sns.service;

import com.likelion.sns.domain.dto.PostDto;
import com.likelion.sns.domain.dto.PostWriteRequest;
import com.likelion.sns.domain.dto.PostWriteResponse;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.PostRepository;
import com.likelion.sns.repository.UserRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class PostServiceTest {
    private PostService postService;
    private PostRepository postRepository= mock(PostRepository.class);
    private UserRepository userRepository= mock(UserRepository.class);

    @BeforeEach
    void setup(){
        postService=new PostService(postRepository, userRepository);
    }

    @Test
    @DisplayName("등록 실패 - user가 존재하지 않을때")
    void post_fail(){
        PostWriteRequest postWriteRequest=PostWriteRequest.builder().title("title").body("body").build();Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));

        Assertions.assertThatThrownBy(()->postService.write(postWriteRequest,"minji"));
    }
    @Test
    @DisplayName("등록 성공")
    void post_success(){
        PostWriteRequest postWriteRequest=PostWriteRequest.builder().title("title").body("body").build();

        Mockito.when(userRepository.findByUserName("minji")).thenReturn(Optional.of(mock(User.class)));
        Mockito.when(postRepository.save(any())).thenReturn(mock(Post.class));

        PostWriteResponse postWriteResponse=postService.write(postWriteRequest, "minji");

        assertEquals(postWriteResponse.getPostId(), 0);
        assertEquals(postWriteResponse.getMessage(), "포스트 등록 완료");

        verify(postRepository).save(any());
    }
    @Test
    @DisplayName("포스트 상세 조회 성공")
    void findPostById_success(){
        Post post=Post.builder()
                .id(1)
                .title("Title")
                .body("Body")
                .user(User.builder().id(1).userName("minji").password("1234").build())
                .build();
        Mockito.when(postRepository.findById(1)).thenReturn(Optional.of(post));

        PostDto postDto=postService.findPostById(1);

        assertEquals(postDto.getId(), post.getId());
        assertEquals(postDto.getTitle(), post.getTitle());
    }
    @Test
    @DisplayName("포스트 상세 조회 실패 - postId가 존재하지 않을때")
    void findPostById_fail(){
        Mockito.when(postRepository.findById(1)).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));
        Assertions.assertThatThrownBy(()->postService.findPostById(1));
    }
}