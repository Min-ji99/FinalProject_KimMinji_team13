package com.likelion.sns.domain.dto;

import com.likelion.sns.domain.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class CommentDto {
    private Long id;
    private String comment;
    private String userName;
    private Integer postId;
    private LocalDateTime createdAt;
    private LocalDateTime lastModifiedAt;

    public static Page<CommentDto> toList(Page<Comment> commentEntities) {
        return commentEntities.map(entity->CommentDto.builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .userName(entity.getUser().getUserName())
                .postId(entity.getPost().getId())
                .createdAt(entity.getCreatedAt())
                .build());
    }
}
