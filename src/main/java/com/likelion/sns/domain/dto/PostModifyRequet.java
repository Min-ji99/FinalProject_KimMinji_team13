package com.likelion.sns.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Builder
@Getter
@AllArgsConstructor
public class PostModifyRequet {
    private String title;
    private String body;
}
