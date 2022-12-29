package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.PostDto;
import com.likelion.sns.domain.dto.PostModifyRequet;
import com.likelion.sns.domain.dto.PostWriteRequest;
import com.likelion.sns.domain.dto.PostResponse;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
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

    private final PostWriteRequest POST_WRITE_REQUEST=PostWriteRequest.builder()
            .title("title")
            .body("body")
            .build();

    private final PostModifyRequet POST_MODIFY_REQUEST=PostModifyRequet.builder()
            .title("modify title")
            .body("modify body")
            .build();
    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void write_success() throws Exception {
        PostResponse postResponse = PostResponse.builder()
                .postId(0)
                .message("포스트 등록 완료")
                .build();

        when(postService.writePost(any(), any())).thenReturn(postResponse);

        mockMvc.perform(post("/api/v1/posts")
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

        mockMvc.perform(post("/api/v1/posts")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }

    /*
    @Test
    @DisplayName("포스트 리스트 조회 - createdAt 기준으로 정렬되어있는지 확인")
    @WithMockUser
    void getPostList() throws Exception{
        mockMvc.perform(get("/api/v1/posts")
                        .param("page", "0")
                        .param("size", "2")
                        .param("sort", "createdAt,desc"))
                .andDo(print())
                .andExpect(status().isOk());
    }*/
    @Test
    @DisplayName("포스트 상세 조회 - id, title, body, userName 존재")
    @WithMockUser
    void getPostById_success() throws Exception{
        PostDto postDto= PostDto.builder()
                .id(1)
                .title("Title")
                .body("Body")
                .userName("minji")
                .createdAt(LocalDateTime.now())
                .lastModifiedAt(LocalDateTime.now())
                .build();

        when(postService.findPostById(any())).thenReturn(postDto);

        mockMvc.perform(get("/api/v1/posts/1")
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

        mockMvc.perform(put("/api/v1/posts/1")
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

        mockMvc.perform(put("/api/v1/posts/1")
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

        mockMvc.perform(put("/api/v1/posts/1")
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

        mockMvc.perform(put("/api/v1/posts/1")
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

        mockMvc.perform(put("/api/v1/posts/1")
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

        mockMvc.perform(delete("/api/v1/posts/1")
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

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 삭제 실패 - 작성자 불일치")
    @WithAnonymousUser
    void delete_fail2() throws Exception {
        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("포스트 삭제 실패 - DB 에러")
    @WithMockUser
    void delete_fail3() throws Exception {
        when(postService.deletePost(any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put("/api/v1/posts/1")
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
}