package com.likelion.sns.domain.dto;

import com.likelion.sns.enums.ErrorCode;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
public class ErrorResponse {
    private ErrorCode errorCode;
    private String message;
}
