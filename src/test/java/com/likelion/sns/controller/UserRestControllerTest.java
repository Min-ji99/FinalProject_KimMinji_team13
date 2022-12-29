package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.*;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.enums.UserRole;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.UserService;
import com.likelion.sns.utils.JwtTokenUtil;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserRestController.class)
class UserRestControllerTest {
    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;
    @MockBean
    JwtTokenUtil jwtTokenUtil;

    private final UserJoinRequest userJoinRequest= UserJoinRequest.builder()
            .userName("minji")
            .password("1234")
            .build();
    private final UserLoginRequest userLoginRequest=UserLoginRequest.builder()
            .userName("minji")
            .password("1234")
            .build();
    private final UserRoleChangeRequest USER_ROLE_CHANGE_REQUEST=UserRoleChangeRequest.builder()
            .role("ADMIN")
            .build();
    private final UserRoleChangeResponse USER_ROLE_CHANGE_RESPONSE=UserRoleChangeResponse.builder()
            .userId(1)
            .role(UserRole.ADMIN)
            .userName("minji")
            .build();
    private final User USER= User.builder()
            .id(1)
            .userName("minji")
            .password("1234")
            .role(UserRole.USER)
            .build();
    private final User ADMIN= User.builder()
            .id(1)
            .userName("admin")
            .password("admin")
            .role(UserRole.ADMIN)
            .build();
    private final String TOKEN="token";
    private final String USERNAME="minji";
    @Test
    @DisplayName("회원가입 성공")
    @WithMockUser
    void join_success() throws Exception {
        UserJoinResponse userJoinResponse=UserJoinResponse.builder().userId(0).userName(USERNAME).build();
        when(userService.join(any())).thenReturn(userJoinResponse);

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
    @Test
    @DisplayName("로그인 실패 - username이 존재하지 않는 경우")
    @WithMockUser
    void login_fail1() throws Exception{

        when(userService.login(any())).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s이 존재하지 않습니다.", userLoginRequest.getUserName())));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isNotFound());
    }
    @Test
    @DisplayName("로그인 실패 - password 오류")
    @WithMockUser
    void login_fail2() throws Exception{

        when(userService.login(any())).thenThrow(new AppException(ErrorCode.INVALID_PASSWORD, String.format("Username 또는 password가 잘못되었습니다.")));

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
    @Test
    @DisplayName("로그인 성공")
    @WithMockUser
    void login_success() throws Exception{
        UserLoginResponse userLoginResponse=UserLoginResponse.builder().jwt(TOKEN).build();

        when(userService.login(any())).thenReturn(userLoginResponse);

        mockMvc.perform(post("/api/v1/users/login")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsBytes(userLoginRequest)))
                .andDo(print())
                .andExpect(status().isOk());
    }
    @Test
    @DisplayName("권한 변경 성공")
    @WithMockUser
    void changeRole_success() throws Exception{
                when(userService.changeRole(any(), any(), any())).thenReturn(USER_ROLE_CHANGE_RESPONSE);

        mockMvc.perform(post("/api/v1/users/1/role/change")
                .with(csrf())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsBytes(USER_ROLE_CHANGE_REQUEST)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.role").value("ADMIN"));
    }
}