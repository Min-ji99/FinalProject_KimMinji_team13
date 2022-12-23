package com.likelion.sns.service;

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
}