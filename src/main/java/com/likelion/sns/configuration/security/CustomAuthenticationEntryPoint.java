package com.likelion.sns.configuration.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.ErrorResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        ErrorCode exception=(ErrorCode) request.getAttribute("exception");
        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_TOKEN, "잘못된 토큰입니다.");

        response.setStatus(HttpStatus.UNAUTHORIZED.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Response.error(errorResponse)));
    }
}
