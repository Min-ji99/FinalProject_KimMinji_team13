package com.likelion.sns.domain.dto;

import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PostWriteRequest {
    private String title;
    private String body;

    public Post toEntity(User user) {
        return Post.builder()
                .title(title)
                .body(body)
                .user(user)
                .build();
    }
}
