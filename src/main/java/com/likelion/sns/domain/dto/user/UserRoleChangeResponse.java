package com.likelion.sns.domain.dto.user;

import com.likelion.sns.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Getter
@NoArgsConstructor
@Builder
public class UserRoleChangeResponse {
    private Integer userId;
    private String userName;
    private UserRole role;
}
