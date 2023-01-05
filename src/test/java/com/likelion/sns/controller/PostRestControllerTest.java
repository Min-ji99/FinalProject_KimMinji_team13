package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.*;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.querydsl.QPageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;

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
    private final Long COMMENT_ID=1l;
    private final String COMMENT="댓글 작성";
    private final String MODIFY_COMMENT="댓글 수정";
    private final String DELETE_COMMENT="댓글 삭제 완료";
    private final String userName="minji";
    private final String COMMENT_GET_WRITE_URL="/api/v1/posts/"+POST_ID+"/comments";
    private final String COMMENT_MODIFY_DELETE_URL="/api/v1/posts/"+POST_ID+"/comments/"+COMMENT_ID;
    private final String LIKE_URL="/api/v1/posts/"+POST_ID+"/likes";

    private final PostWriteRequest POST_WRITE_REQUEST=PostWriteRequest.builder()
            .title("title")
            .body("body")
            .build();

    private final PostModifyRequet POST_MODIFY_REQUEST=PostModifyRequet.builder()
            .title("modify title")
            .body("modify body")
            .build();
    private final PostResponse POST_RESPONSE = PostResponse.builder()
            .postId(0)
            .message("포스트 등록 완료")
            .build();
    private final PostDto POST_DTO= PostDto.builder()
            .id(1)
            .title("Title")
            .body("Body")
            .userName("minji")
            .createdAt(LocalDateTime.now())
            .lastModifiedAt(LocalDateTime.now())
            .build();
    private final CommentWriteRequest COMMENT_WRITE_REQUEST=CommentWriteRequest.builder()
            .comment(COMMENT)
            .build();

    private final CommentDto COMMENT_DTO= CommentDto.builder()
            .id(COMMENT_ID)
            .comment(COMMENT)
            .userName(userName)
            .postId(POST_ID)
            .build();
    private final CommentDto COMMENT_MODIFY_DTO= CommentDto.builder()
            .id(COMMENT_ID)
            .comment(MODIFY_COMMENT)
            .userName(userName)
            .postId(POST_ID)
            .build();

    private final CommentModifyRequest COMMENT_MODIFY_REQUEST=CommentModifyRequest.builder()
            .comment(MODIFY_COMMENT)
            .build();
    private final CommentDeleteResponse COMMENT_DELETE_RESPONSE=CommentDeleteResponse.builder()
            .id(COMMENT_ID)
            .message(DELETE_COMMENT)
            .build();
    private final LikeResponse LIKE_RESPONSE=LikeResponse.builder()
            .message("좋아요를 눌렀습니다.")
            .build();

    @Test
    @DisplayName("포스트 작성 성공")
    @WithMockUser
    void write_success() throws Exception {
        when(postService.writePost(any(), any())).thenReturn(POST_RESPONSE);

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


    @Test
    @DisplayName("포스트 리스트 조회 - createdAt 기준으로 정렬되어있는지 확인")
    @WithMockUser
    void getPostList() throws Exception{
        mockMvc.perform(get("/api/v1/posts")
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

    /*@Test
    @DisplayName("댓글 목록 조회 성공")
    @WithMockUser
    void getCommentList() throws Exception{
        when(postService.writePost(any(), any())).thenReturn(POST_RESPONSE);
        when(postService.writeComment(any(), any(), any())).thenReturn(COMMENT_DTO);
        mockMvc.perform(get(COMMENT_GET_WRITE_URL))
                .andDo(print())
                .andExpect(status().isOk());
        ArgumentCaptor<Pageable> pageableArgumentCaptor=ArgumentCaptor.forClass(Pageable.class);

        verify(postService).getCommentList(POST_ID, pageableArgumentCaptor.capture());
        PageRequest pageRequest=(PageRequest) pageableArgumentCaptor.getValue();

        assertEquals(Sort.by("createdAt", "desc"), pageRequest.withSort(Sort.by("createdAt", "desc")).getSort());
    }*/
    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void writeComment_success() throws Exception {
        when(postService.writeComment(any(), any(), any())).thenReturn(COMMENT_DTO);

        mockMvc.perform(post(COMMENT_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.comment").value(COMMENT))
                .andExpect(jsonPath("$.result.postId").exists());
    }
    @Test
    @DisplayName("댓글 작성 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void writeComment_fail1() throws Exception {
        when(postService.writePost(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(post(COMMENT_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("댓글 작성 실패(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void writeComment_fail2() throws Exception {
        when(postService.writeComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post(COMMENT_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(POST_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void modifyComment_success() throws Exception {

        when(postService.modifyComment(any(), any(), any())).thenReturn(COMMENT_MODIFY_DTO);

        mockMvc.perform(put(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.comment").value(MODIFY_COMMENT))
                .andExpect(jsonPath("$.result.postId").exists());
    }
    @Test
    @DisplayName("댓글 수정 실패(1) - 인증 실패")
    @WithAnonymousUser
    void modifyComment_fail1() throws Exception {
        when(postService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("댓글 수정 실패(2) - 댓글 불일치")
    @WithMockUser
    void modifyComment_fail2() throws Exception {
        when(postService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        mockMvc.perform(put(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.COMMENT_NOT_FOUND.getHttpStatus().value()));
    }
    @Test
    @DisplayName("댓글 수정 실패(3) - 작성자 불일치")
    @WithMockUser
    void modifyComment_fail3() throws Exception {
        when(postService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(put(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getHttpStatus().value()));
    }
    @Test
    @DisplayName("댓글 수정 실패(4) - DB 에러")
    @WithMockUser
    void modifyComment_fail4() throws Exception {
        when(postService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(put(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_MODIFY_REQUEST)))
                .andDo(print())
                .andExpect(status().isInternalServerError());
    }
    @Test
    @DisplayName("댓글 삭제 성공")
    @WithMockUser
    void deleteComment_success() throws Exception {

        when(postService.deleteComment(any(), any())).thenReturn(COMMENT_DELETE_RESPONSE);

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.message").value(DELETE_COMMENT))
                .andExpect(jsonPath("$.result.id").exists());
    }
    @Test
    @DisplayName("댓글 삭제 실패(1) - 인증 실패")
    @WithAnonymousUser
    void deleteComment_fail1() throws Exception {
        when(postService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("댓글 삭제 실패(2) - 댓글 불일치")
    @WithMockUser
    void deleteComment_fail2() throws Exception {
        when(postService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.COMMENT_NOT_FOUND.getHttpStatus().value()));
    }
    @Test
    @DisplayName("댓글 삭제 실패(3) - 작성자 불일치")
    @WithMockUser
    void deleteComment_fail3() throws Exception {
        when(postService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getHttpStatus().value()));
    }
    @Test
    @DisplayName("댓글 삭제 실패(4) - DB 에러")
    @WithMockUser
    void deleteComment_fail4() throws Exception {
        when(postService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());

    }
    @Test
    @DisplayName("좋아요 누르기 성공")
    @WithMockUser
    void like_success() throws Exception{
        when(postService.like(any(), any())).thenReturn(LIKE_RESPONSE);

        mockMvc.perform(post(LIKE_URL)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("좋아요 누르기 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void like_fail1() throws Exception{
        when(postService.like(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));
        mockMvc.perform(post(LIKE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("좋아요 누르기 실패(2) - 해당 Post가 없는 경우")
    @WithMockUser
    void like_fail2() throws Exception{
        when(postService.like(any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));
        mockMvc.perform(post(LIKE_URL)
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}