package com.likelion.sns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.AlarmResponse;
import com.likelion.sns.domain.dto.comment.CommentDto;
import com.likelion.sns.enums.AlarmType;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.AlarmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.*;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AlarmRestController.class)
class AlarmRestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    MockMvc mockMvc;

    @MockBean
    AlarmService alarmService;

    private final String ALARM_URL="/api/v1/alarms";
    private final Integer ALARM_ID=1;
    private final Integer ALARM_ID2=2;
    private final Integer POST_ID=1;
    private final Integer USER_ID=1;
    private final String USERNAME="user";
    private final Pageable pageable= PageRequest.of(0, 20, Sort.Direction.DESC, "createdAt");
    private final AlarmResponse ALARM_RESPONSE1=AlarmResponse.builder()
            .id(ALARM_ID)
            .alarmType(AlarmType.NEW_COMMENT_ON_POST)
            .targetId(POST_ID)
            .fromUserId(USER_ID)
            .text(AlarmType.NEW_COMMENT_ON_POST.getMessage())
            .createdAt(LocalDateTime.of(2023, 1, 7, 12, 13, 50))
            .build();

    @Test
    @DisplayName("알람 목록 조회 성공")
    @WithMockUser
    void alarm_success() throws Exception {
        Page<AlarmResponse> responsePage = new PageImpl<>(List.of(ALARM_RESPONSE1));
        when(alarmService.getAlarmList(pageable, USERNAME)).thenReturn(responsePage);

        mockMvc.perform(get(ALARM_URL)
                                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result.content[0].targetId").value(POST_ID))
                .andExpect(jsonPath("$.result.content[0].text").value(AlarmType.NEW_COMMENT_ON_POST.getMessage()));
        verify(alarmService).getAlarmList(pageable, USERNAME);
    }
    @Test
    @DisplayName("알람 목족 조회 실패 - 로그인하지 않은 경우")
    @WithAnonymousUser
    void alarm_fail() throws Exception {
        when(alarmService.getAlarmList(any(), any())).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));

        mockMvc.perform(get(ALARM_URL)
                        .with(csrf()))
                .andDo(print())
                .andExpect(status().isUnauthorized());
    }
}