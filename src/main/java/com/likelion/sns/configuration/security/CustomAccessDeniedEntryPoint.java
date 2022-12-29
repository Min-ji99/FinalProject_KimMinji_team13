package com.likelion.sns.configuration.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.likelion.sns.domain.dto.ErrorResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.enums.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component
public class CustomAccessDeniedEntryPoint implements AccessDeniedHandler {

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();

        ErrorResponse errorResponse = new ErrorResponse(ErrorCode.INVALID_PERMISSION, ErrorCode.INVALID_PERMISSION.getMessage());

        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType("application/json");
        response.setCharacterEncoding("utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(Response.error(errorResponse)));
    }
}
