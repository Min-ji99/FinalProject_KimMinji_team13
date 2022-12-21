package com.likelion.sns.service;

import com.likelion.sns.domain.dto.UserJoinRequest;
import com.likelion.sns.domain.dto.UserJoinResponse;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;

    private final BCryptPasswordEncoder encoder;

    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder) {
        this.userRepository = userRepository;
        this.encoder = encoder;
    }

    public UserJoinResponse join(UserJoinRequest dto) {
        userRepository.findByUserName(dto.getUserName())
                .ifPresent(user->{
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s는 존재하는 이름입니다.", dto.getUserName()));
                });

        User user=userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return UserJoinResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .build();
    }
}
