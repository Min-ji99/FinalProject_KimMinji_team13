package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.comment.CommentDeleteResponse;
import com.likelion.sns.domain.dto.comment.CommentDto;
import com.likelion.sns.domain.dto.comment.CommentModifyRequest;
import com.likelion.sns.domain.dto.comment.CommentWriteRequest;
import com.likelion.sns.domain.dto.post.PostDto;
import com.likelion.sns.domain.dto.post.PostModifyRequet;
import com.likelion.sns.domain.dto.post.PostResponse;
import com.likelion.sns.domain.dto.post.PostWriteRequest;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PostRestController.class)
class PostRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    PostService postService;
    private final Integer POST_ID=1;
    private final String userName="minji";
    private final String TITLE="title";
    private final String BODY="body";
    private final String MODIFY_TITLE="modify title";
    private final String MODIFY_BODY="modify body";
    private final String POST_GET_WRITE_URL="/api/v1/posts";
    private final String POST_MODIFY_DELETE_URL="/api/v1/posts/"+POST_ID;
    private final String MY_FEED_URL="/api/v1/posts/my";

    private final PostWriteRequest POST_WRITE_REQUEST=PostWriteRequest.builder()
            .title(TITLE)
            .body(BODY)
            .build();
    private final PostModifyRequet POST_MODIFY_REQUEST=PostModifyRequet.builder()
            .title(MODIFY_TITLE)
            .body(MODIFY_BODY)
            .build();
    private final PostResponse POST_RESPONSE = PostResponse.builder()
            .postId(0)
            .message("포스트 등록 완료")
            .build();
    private final PostDto POST_DTO= PostDto.builder()
            .id(POST_ID)
            .title(TITLE)
            .body(BODY)
            .userName(userName)
            .createdAt(LocalDateTime.now())
            .lastModifiedAt(LocalDateTime.now())
            .build();

    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void write_success() throws Exception {
        when(postService.writePost(any(), any())).thenReturn(POST_RESPONSE);

        mockMvc.perform(post(POST_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists());
    }
    @Test
    @DisplayName("포스트 작성 실패 - 인증 실패")
    @WithAnonymousUser
    void write_fail() throws Exception {
        when(postService.writePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(post(POST_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }


    @Test
    @DisplayName("포스트 리스트 조회 - createdAt 기준으로 정렬되어있는지 확인")
    @WithMockUser
    void getPostList() throws Exception{
        mockMvc.perform(get(POST_GET_WRITE_URL)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Pageable> pageableArgumentCaptor=ArgumentCaptor.forClass(Pageable.class);

        verify(postService).getPostlist(pageableArgumentCaptor.capture());
        PageRequest pageRequest=(PageRequest) pageableArgumentCaptor.getValue();

        assertEquals(Sort.by("createdAt", "desc"), pageRequest.withSort(Sort.by("createdAt", "desc")).getSort());
    }
    @Test
    @DisplayName("포스트 상세 조회 - id, title, body, userName 존재")
    @WithMockUser
    void getPostById_success() throws Exception{
        when(postService.findPostById(any())).thenReturn(POST_DTO);

        mockMvc.perform(get(POST_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.id").exists())
                .andExpect(jsonPath("$.result.title").exists())
                .andExpect(jsonPath("$.result.userName").exists())
                .andExpect(jsonPath("$.result.body").exists())
                .andExpect(jsonPath("$.result.createdAt").exists())
                .andExpect(jsonPath("$.result.lastModifiedAt").exists());
    }
    @Test
    @DisplayName("포스트 수정 성공")
    @WithMockUser
    void modify_success() throws Exception {
        PostResponse postResponse = PostResponse.builder()
                .postId(1)
                .message("포스트 수정 완료")
                .build();
        when(postService.modifyPost(any(), any(), any())).thenReturn(postResponse);

        mockMvc.perform(put(POST_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").exists())
                .andExpect(jsonPath("$.result.postId").exists());
    }
    @Test
    @DisplayName("포스트 수정 실패 - 인증 실패")
    @WithAnonymousUser
    void modify_fail1() throws Exception {
        when(postService.modifyPost(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put(POST_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 수정 실패 - post 존재하지 않을때")
    @WithMockUser
    void modify_fail2() throws Exception {
        when(postService.modifyPost(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(put(POST_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("포스트 수정 실패 - 작성자가 일치하지 않을때")
    @WithMockUser
    void modify_fail3() throws Exception {
        when(postService.modifyPost(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put(POST_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 수정 실패 - DB 에러")
    @WithMockUser
    void modify_fail4() throws Exception {
        when(postService.modifyPost(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put(POST_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    @DisplayName("포스트 삭제 성공")
    @WithMockUser
    void delete_success() throws Exception {
        PostResponse postResponse = PostResponse.builder()
                .postId(1)
                .message("포스트 삭제 완료")
                .build();
        when(postService.deletePost(any(), any())).thenReturn(postResponse);

        mockMvc.perform(delete(POST_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value("포스트 삭제 완료"))
                .andExpect(jsonPath("$.result.postId").exists());
    }
    @Test
    @DisplayName("포스트 삭제 실패 - 인증 실패")
    @WithAnonymousUser
    void delete_fail1() throws Exception {
        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete(POST_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 삭제 실패 - 작성자 불일치")
    @WithAnonymousUser
    void delete_fail2() throws Exception {
        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete(POST_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 삭제 실패 - DB 에러")
    @WithMockUser
    void delete_fail3() throws Exception {
        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put(POST_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    @DisplayName("마이피드 조회 성공")
    @WithMockUser
    void myFeed_success() throws Exception{
        mockMvc.perform(get(MY_FEED_URL)
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "createdAt,desc")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Pageable> pageableArgumentCaptor=ArgumentCaptor.forClass(Pageable.class);

        verify(postService).getMyFeed(any(), pageableArgumentCaptor.capture());
        PageRequest pageRequest=(PageRequest) pageableArgumentCaptor.getValue();

        assertEquals(Sort.by("createdAt", "desc"), pageRequest.withSort(Sort.by("createdAt", "desc")).getSort());
    }
    @Test
    @DisplayName("마이피드 조회 실패 - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void myFeed_fail1() throws Exception {
        when(postService.getMyFeed(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_TOKEN, ""));

        mockMvc.perform(get(MY_FEED_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}