package com.likelion.sns.domain.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.likelion.sns.domain.dto.post.PostDto;
import com.likelion.sns.domain.entity.Alarm;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.time.LocalDateTime;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class AlarmResponse {
    private Integer id;
    private AlarmType alarmType;
    private Integer fromUserId;
    private Integer targetId;
    private String text;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy/dd/mm hh:mm:ss")
    private LocalDateTime createdAt;

    public static Page<AlarmResponse> toList(Page<Alarm> alarmEntities) {
        Page<AlarmResponse> alarmResponses= alarmEntities.map(entity->AlarmResponse.builder()
                .id(entity.getId())
                .alarmType(entity.getAlarmType())
                .fromUserId(entity.getFromUserId())
                .targetId(entity.getTargetId())
                .text(entity.getAlarmType().getMessage())
                .createdAt(entity.getCreatedAt())
                .build());

        return alarmResponses;
    }
}
