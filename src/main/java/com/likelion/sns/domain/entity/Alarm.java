package com.likelion.sns.domain.entity;

import com.likelion.sns.enums.AlarmType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Alarm extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Enumerated(EnumType.STRING)
    private AlarmType alarmType;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    private Integer fromUserId;
    private Integer targetId;

    public static Alarm createAlarm(AlarmType alarmType, User fromUser, Post post){
        return Alarm.builder()
                .alarmType(alarmType)
                .user(post.getUser())
                .fromUserId(fromUser.getId())
                .targetId(post.getId())
                .build();

    }
}
