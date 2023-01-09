package com.likelion.sns.domain.dto.comment;

import com.fasterxml.jackson.annotation.JsonFormat;
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
    private Integer id;
    private String comment;
    private String userName;
    private Integer postId;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/dd/mm hh:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/dd/mm hh:mm:ss")
    private LocalDateTime lastModifiedAt;

    public static Page<CommentDto> toList(Page<Comment> commentEntities) {
        return commentEntities.map(entity->CommentDto.builder()
                .id(entity.getId())
                .comment(entity.getComment())
                .userName(entity.getUser().getUserName())
                .postId(entity.getPost().getId())
                .createdAt(entity.getCreatedAt())
                .lastModifiedAt(entity.getLastModifiedAt())
                .build());
    }
}
