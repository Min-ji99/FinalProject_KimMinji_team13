package com.likelion.sns.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.sns.domain.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import net.bytebuddy.asm.Advice;
import org.springframework.data.domain.Page;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class PostDto {
    private Integer id;
    private String title;
    private String body;
    private String userName;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/dd/mm hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/dd/mm hh:mm:ss")
    private LocalDateTime lastModifiedAt;

    public static Page<PostDto> toList(Page<Post> postEntities) {
        Page<PostDto> posts= postEntities.map(entity->PostDto.builder()
                .id(entity.getId())
                .title(entity.getTitle())
                .body(entity.getBody())
                .userName(entity.getUser().getUserName())
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .build());

        return posts;
    }
}
