package com.likelion.sns.service;

import com.likelion.sns.domain.dto.post.PostResponse;
import com.likelion.sns.domain.dto.user.UserRoleChangeRequest;
import com.likelion.sns.domain.dto.user.UserRoleChangeResponse;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.enums.UserRole;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.UserRepository;
import com.likelion.sns.utils.JwtTokenUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;

class UserServiceTest {
    private UserService userService;
    private UserRepository userRepository= mock(UserRepository.class);
    private final BCryptPasswordEncoder encoder=mock(BCryptPasswordEncoder.class);
    private final JwtTokenUtil jwtTokenUtil=mock(JwtTokenUtil.class);
    private final String ADMIN_NAME="admin";
    private final String USER_NAME="minji";
    private final Integer USER_ID=2;
    private final User ADMIN=User.builder()
            .id(1)
            .role(UserRole.ADMIN)
            .userName(ADMIN_NAME)
            .password("admin")
            .build();
    private final User USER=User.builder()
            .id(2)
            .role(UserRole.USER)
            .userName(USER_NAME)
            .password("1234")
            .build();
    private final UserRoleChangeRequest USER_ROLE_CHANGE_REQUEST= UserRoleChangeRequest.builder()
            .role("admin")
            .build();
    @BeforeEach
    void setup(){
        userService=new UserService(userRepository, encoder, jwtTokenUtil);
    }

    @Test
    @DisplayName("권한 변경 실패 - ADMIN이 아닌 경우")
    void changeRole_fail1(){
        Mockito.when(userRepository.findByUserName(USER_NAME)).thenThrow(new AppException(ErrorCode.INVALID_PERMISSION, ""));
        Mockito.when(userRepository.findById(USER_ID)).thenReturn(Optional.of(USER));
        AppException appException=assertThrows(AppException.class, ()->userService.changeRole(USER_ROLE_CHANGE_REQUEST, USER_ID, USER_NAME));
        assertEquals(appException.getErrorCode(), ErrorCode.INVALID_PERMISSION);
    }
    @Test
    @DisplayName("권한 변경 실패 - 권한 변경할 User Id가 존재하지 않는 경우")
    void changeRole_fail2(){
        Mockito.when(userRepository.findByUserName(ADMIN_NAME)).thenReturn(Optional.of(ADMIN));
        Mockito.when(userRepository.findById(USER_ID)).thenThrow(new AppException(ErrorCode.USERNAME_NOT_FOUND, ""));
        AppException appException=assertThrows(AppException.class, ()->userService.changeRole(USER_ROLE_CHANGE_REQUEST, USER_ID, ADMIN_NAME));
        assertEquals(appException.getErrorCode(), ErrorCode.USERNAME_NOT_FOUND);
    }
}