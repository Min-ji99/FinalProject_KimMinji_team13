package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.LikeService;
import com.likelion.sns.service.PostService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(LikeRestController.class)
class LikeRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    LikeService likeService;
    private final Integer POST_ID=1;
    private final String LIKE_RESPONSE="좋아요를 눌렀습니다.";
    private final String LIKE_URL="/api/v1/posts/"+POST_ID+"/likes";
    @Test
    @DisplayName("좋아요 누르기 성공")
    @WithMockUser
    void like_success() throws Exception{
        when(likeService.like(any(), any())).thenReturn(LIKE_RESPONSE);

        mockMvc.perform(post(LIKE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("좋아요 누르기 실패(1) - 로그인 하지 않은 경우")
    @WithAnonymousUser
    void like_fail1() throws Exception{
        when(likeService.like(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));
        mockMvc.perform(post(LIKE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("좋아요 누르기 실패(2) - 해당 Post가 없는 경우")
    @WithMockUser
    void like_fail2() throws Exception{
        when(likeService.like(any(), any())).thenThrow(new AppException(ErrorCode.POST_NOT_FOUND, ""));
        mockMvc.perform(post(LIKE_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
}