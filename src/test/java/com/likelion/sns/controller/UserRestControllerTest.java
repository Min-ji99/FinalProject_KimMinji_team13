package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.domain.dto.UserJoinRequest;
import com.likelion.sns.domain.dto.UserJoinResponse;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.UserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class UserRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;

    UserJoinRequest userJoinRequest= UserJoinRequest.builder()
            .userName("minji")
            .password("1234")
            .build();

    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join_success() throws Exception {
        //when(userService.join(any())).thenReturn(mock(UserJoinResponse.class));
        mockMvc.perform(post("/api/v1/users/join")
                        .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("회원가입 실패 - username 중복")
    @WithMockUser
    void join_fail() throws Exception {
        when(userService.join(any())).thenThrow(new AppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s는 존재하는 이름입니다.", userJoinRequest.getUserName())));

        mockMvc.perform(post("/api/v1/users/join")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(userJoinRequest)))
                .andDo(print())
                .andExpect(status().isConflict());
    }
}