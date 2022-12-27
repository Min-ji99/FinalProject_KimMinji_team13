package com.likelion.sns.enums;

import com.likelion.sns.exception.AppException;

public enum UserRole {
    ADMIN, USER;

    public static UserRole of(String role){
        role=role.toUpperCase();
        for(UserRole userRole : values()){
            if(userRole.name().equals(role))
                return userRole;
        }
        throw new AppException(ErrorCode.ROLE_NOT_FOUND, String.format("role %s는 존재하지 않습니다.", role));
    }

}
