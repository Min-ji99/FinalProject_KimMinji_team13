package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.comment.CommentDeleteResponse;
import com.likelion.sns.domain.dto.comment.CommentDto;
import com.likelion.sns.domain.dto.comment.CommentModifyRequest;
import com.likelion.sns.domain.dto.comment.CommentWriteRequest;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.CommentService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CommentRestController.class)
class CommentRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    CommentService commentService;

    private final Integer POST_ID=1;
    private final Integer COMMENT_ID=1;
    private final Integer COMMENT_ID2=2;
    private final String COMMENT1="댓글 작성1";
    private final String COMMENT2="댓글 작성2";
    private final String MODIFY_COMMENT="댓글 수정";
    private final String DELETE_COMMENT="댓글 삭제 완료";
    private final String userName="minji";
    private final Pageable pageable= PageRequest.of(0, 10, Sort.Direction.DESC, "createdAt");
    private final String COMMENT_GET_WRITE_URL="/api/v1/posts/"+POST_ID+"/comments";
    private final String COMMENT_MODIFY_DELETE_URL="/api/v1/posts/"+POST_ID+"/comments/"+COMMENT_ID;
    private final CommentWriteRequest COMMENT_WRITE_REQUEST=CommentWriteRequest.builder()
            .comment(COMMENT1)
            .build();

    private final CommentDto COMMENT_DTO= CommentDto.builder()
            .id(COMMENT_ID)
            .comment(COMMENT1)
            .userName(userName)
            .postId(POST_ID)
            .createdAt(LocalDateTime.of(2023, 1, 6, 12, 13, 50))
            .lastModifiedAt(LocalDateTime.of(2023, 1, 6, 12, 13, 50))
            .build();
    private final CommentDto COMMENT_DTO2= CommentDto.builder()
            .id(COMMENT_ID2)
            .comment(COMMENT2)
            .userName(userName)
            .postId(POST_ID)
            .createdAt(LocalDateTime.of(2023, 1, 5, 12, 13, 50))
            .lastModifiedAt(LocalDateTime.of(2023, 1, 5, 12, 13, 50))
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

    @Test
    @DisplayName("댓글 목록 조회 성공")
    @WithMockUser
    void getCommentList() throws Exception{
        Page<CommentDto> commentPage=new PageImpl<>(List.of(COMMENT_DTO, COMMENT_DTO2));
        when(commentService.getCommentList(POST_ID, pageable)).thenReturn(commentPage);
        mockMvc.perform(get(COMMENT_GET_WRITE_URL))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content").exists())
                .andExpect(jsonPath("$.result.content[0].comment").value(COMMENT1))
                .andExpect(jsonPath("$.result.content[0].postId").value(POST_ID))
                .andExpect(jsonPath("$.result.content[1].comment").value(COMMENT2));
    }
    @Test
    @DisplayName("댓글 작성 성공")
    @WithMockUser
    void writeComment_success() throws Exception {
        when(commentService.writeComment(any(), any(), any())).thenReturn(COMMENT_DTO);

        mockMvc.perform(post(COMMENT_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.comment").value(COMMENT1))
                .andExpect(jsonPath("$.result.postId").exists());
    }
    @Test
    @DisplayName("댓글 작성 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void writeComment_fail1() throws Exception {
        when(commentService.writeComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(post(COMMENT_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("댓글 작성 실패(2) - 게시물이 존재하지 않는 경우")
    @WithMockUser
    void writeComment_fail2() throws Exception {
        when(commentService.writeComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));

        mockMvc.perform(post(COMMENT_GET_WRITE_URL)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(COMMENT_WRITE_REQUEST)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("댓글 수정 성공")
    @WithMockUser
    void modifyComment_success() throws Exception {

        when(commentService.modifyComment(any(), any(), any())).thenReturn(COMMENT_MODIFY_DTO);

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
        when(commentService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

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
        when(commentService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

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
        when(commentService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

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
        when(commentService.modifyComment(any(), any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

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

        when(commentService.deleteComment(any(), any())).thenReturn(COMMENT_DELETE_RESPONSE);

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
        when(commentService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("댓글 삭제 실패(2) - 댓글 불일치")
    @WithMockUser
    void deleteComment_fail2() throws Exception {
        when(commentService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.COMMENT_NOT_FOUND, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.COMMENT_NOT_FOUND.getHttpStatus().value()));
    }
    @Test
    @DisplayName("댓글 삭제 실패(3) - 작성자 불일치")
    @WithMockUser
    void deleteComment_fail3() throws Exception {
        when(commentService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().is(ErrorCode.INVALID_PERMISSION.getHttpStatus().value()));
    }
    @Test
    @DisplayName("댓글 삭제 실패(4) - DB 에러")
    @WithMockUser
    void deleteComment_fail4() throws Exception {
        when(commentService.deleteComment(any(), any())).thenThrow(new AppException(ErrorCode.DATABASE_ERROR, ""));

        mockMvc.perform(delete(COMMENT_MODIFY_DELETE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isInternalServerError());

    }

}