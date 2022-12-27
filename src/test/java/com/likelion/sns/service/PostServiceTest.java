package com.likelion.sns.service;

import com.likelion.sns.domain.dto.PostDto;
import com.likelion.sns.domain.dto.PostModifyRequet;
import com.likelion.sns.domain.dto.PostWriteRequest;
import com.likelion.sns.domain.dto.PostResponse;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.CommentRepository;
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
    private CommentRepository commentRepository=mock(CommentRepository.class);

    private final User USER1=User.builder()
            .id(1)
            .userName("minji")
            .password("1234")
            .build();
    private final User USER2=User.builder()
            .id(2)
            .userName("jimin")
            .password("9876")
            .build();
    private final PostWriteRequest postWriteRequest=PostWriteRequest.builder()
            .title("title")
            .body("body")
            .build();
    private final Post POST=Post.builder()
            .title("Title")
            .body("Body")
            .user(USER1)
            .build();
    private final PostModifyRequet MODIFYREQUEST= PostModifyRequet.builder()
            .title("modify title")
            .body("modify body")
            .build();

    @BeforeEach
    void setup(){
        postService=new PostService(postRepository, userRepository, commentRepository);
    }

    @Test
    @DisplayName("등록 실패 - user가 존재하지 않을때")
    void post_fail(){
        Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));

        Assertions.assertThatThrownBy(()->postService.write(postWriteRequest,USER1.getUserName()));
    }
    @Test
    @DisplayName("등록 성공")
    void post_success(){
        Mockito.when(userRepository.findByUserName(USER1.getUserName())).thenReturn(Optional.of(USER1));
        Mockito.when(postRepository.save(any())).thenReturn(POST);

        PostResponse postResponse =postService.write(postWriteRequest, USER1.getUserName());

        assertEquals(postResponse.getPostId(), USER1.getId());
        assertEquals(postResponse.getMessage(), "포스트 등록 완료");

        verify(postRepository).save(any());
    }
    @Test
    @DisplayName("포스트 상세 조회 성공")
    void findPostById_success(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));

        PostDto postDto=postService.findPostById(POST.getId());

        assertEquals(postDto.getId(), POST.getId());
        assertEquals(postDto.getTitle(), POST.getTitle());
    }
    @Test
    @DisplayName("포스트 상세 조회 실패 - postId가 존재하지 않을때")
    void findPostById_fail(){
        Mockito.when(postRepository.findById(POST.getId())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));
        Assertions.assertThatThrownBy(()->postService.findPostById(POST.getId()));
    }
    @Test
    @DisplayName("포스트 수정 - 포스트 존재하지 않을 때")
    void modify_fail_no_post(){
        Mockito.when(postRepository.findById(POST.getId())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modify(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }
    @Test
    @DisplayName("포스트 수정 - 유저가 존재하지 않을 때 ")
    void modify_fail_no_user(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));
        Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modify(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
    }
    @Test
    @DisplayName("포스트 수정 - 작성자와 사용자가 일치하지 않을 때 ")
    void modify_fail_not_match(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));
        Mockito.when(userRepository.findByUserName(USER2.getUserName())).thenReturn(Optional.of(USER2));
        AppException appException=assertThrows(AppException.class, ()->postService.modify(POST.getId(), MODIFYREQUEST, USER2.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.INVALID_PERMISSION);
    }
    @Test
    @DisplayName("포스트 삭제 - 유저가 존재하지 않을 때 ")
    void delete_fail_no_user(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));
        Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modify(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
    }
    @Test
    @DisplayName("포스트 삭제 - 포스트 존재하지 않을 때")
    void delete_fail_no_post(){
        Mockito.when(postRepository.findById(POST.getId())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modify(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }
}