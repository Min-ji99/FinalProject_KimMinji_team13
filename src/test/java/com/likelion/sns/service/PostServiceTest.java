package com.likelion.sns.service;

import com.likelion.sns.domain.dto.post.PostDto;
import com.likelion.sns.domain.dto.post.PostModifyRequet;
import com.likelion.sns.domain.dto.post.PostWriteRequest;
import com.likelion.sns.domain.dto.post.PostResponse;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.CommentRepository;
import com.likelion.sns.repository.LikeRepository;
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
    private LikeRepository likeRepository=mock(LikeRepository.class);

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
            .id(1)
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
        postService=new PostService(postRepository, userRepository, commentRepository, likeRepository);
    }

    @Test
    @DisplayName("?????? ?????? - user??? ???????????? ?????????")
    void post_fail(){
        Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));

        Assertions.assertThatThrownBy(()->postService.writePost(postWriteRequest,USER1.getUserName()));
    }
    @Test
    @DisplayName("?????? ??????")
    void post_success(){
        Mockito.when(userRepository.findByUserName(USER1.getUserName())).thenReturn(Optional.of(USER1));
        Mockito.when(postRepository.save(any())).thenReturn(POST);

        PostResponse postResponse =postService.writePost(postWriteRequest, USER1.getUserName());

        assertEquals(postResponse.getPostId(), USER1.getId());
        assertEquals(postResponse.getMessage(), "????????? ?????? ??????");

        //verify(postRepository).save(any());
    }
    @Test
    @DisplayName("????????? ?????? ?????? ??????")
    void findPostById_success(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));

        PostDto postDto=postService.findPostById(POST.getId());

        assertEquals(postDto.getId(), POST.getId());
        assertEquals(postDto.getTitle(), POST.getTitle());
    }
    @Test
    @DisplayName("????????? ?????? ?????? ?????? - postId??? ???????????? ?????????")
    void findPostById_fail(){
        Mockito.when(postRepository.findById(POST.getId())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, String.format("?????? ???????????? ???????????? ????????????.")));
        Assertions.assertThatThrownBy(()->postService.findPostById(POST.getId()));
    }
    @Test
    @DisplayName("????????? ?????? - ????????? ???????????? ?????? ???")
    void modify_fail_no_post(){
        Mockito.when(postRepository.findById(POST.getId())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modifyPost(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }
    @Test
    @DisplayName("????????? ?????? - ????????? ???????????? ?????? ??? ")
    void modify_fail_no_user(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));
        Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modifyPost(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
    }
    @Test
    @DisplayName("????????? ?????? - ???????????? ???????????? ???????????? ?????? ??? ")
    void modify_fail_not_match(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));
        Mockito.when(userRepository.findByUserName(USER2.getUserName())).thenReturn(Optional.of(USER2));
        AppException appException=assertThrows(AppException.class, ()->postService.modifyPost(POST.getId(), MODIFYREQUEST, USER2.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.INVALID_PERMISSION);
    }
    @Test
    @DisplayName("????????? ?????? - ????????? ???????????? ?????? ??? ")
    void delete_fail_no_user(){
        Mockito.when(postRepository.findById(POST.getId())).thenReturn(Optional.of(POST));
        Mockito.when(userRepository.findByUserName(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modifyPost(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
    }
    @Test
    @DisplayName("????????? ?????? - ????????? ???????????? ?????? ???")
    void delete_fail_no_post(){
        Mockito.when(postRepository.findById(POST.getId())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->postService.modifyPost(POST.getId(), MODIFYREQUEST, USER1.getUserName()));
        assertEquals(appException.getErrorCode(), ErrorCode.POST_NOT_FOUND);
    }
}