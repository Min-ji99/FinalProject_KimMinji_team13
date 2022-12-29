package com.likelion.sns.controller;

import com.likelion.sns.service.AlgorithmService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(HelloController.class)
class HelloControllerTest {
    @Autowired
    MockMvc mockMvc;
    @MockBean
    AlgorithmService algorithmService;

    @Test
    @WithMockUser
    @DisplayName("자릿수 합")
    void sumOfDigit() throws Exception{
        when(algorithmService.sumOfDigits(687)).thenReturn(21);

        mockMvc.perform(get("/api/v1/hello/687")
                .with(csrf()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(content().string("21"));
    }
}