package com.likelion.sns.domain.dto;

import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserJoinRequest {
    private String userName;
    private String password;

    public User toEntity(String encodingPassword) {
        return User.builder()
                .userName(userName)
                .password(encodingPassword)
                .role(UserRole.USER)
                .build();
    }
}
