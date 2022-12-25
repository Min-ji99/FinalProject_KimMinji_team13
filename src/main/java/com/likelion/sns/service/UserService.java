package com.likelion.sns.service;

import com.likelion.sns.domain.dto.UserJoinRequest;
import com.likelion.sns.domain.dto.UserJoinResponse;
import com.likelion.sns.domain.dto.UserLoginRequest;
import com.likelion.sns.domain.dto.UserLoginResponse;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.UserRepository;
import com.likelion.sns.utils.JwtTokenUtil;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;
    private final JwtTokenUtil jwtTokenUtil;
    public UserService(UserRepository userRepository, BCryptPasswordEncoder encoder, JwtTokenUtil jwtTokenUtil) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.jwtTokenUtil = jwtTokenUtil;
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

    public UserLoginResponse login(UserLoginRequest dto) {
        User user=userRepository.findByUserName(dto.getUserName())
                .orElseThrow(()-> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s이 존재하지 않습니다.", dto.getUserName())));

        if(!encoder.matches(dto.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, String.format("Username 또는 password가 잘못되었습니다."));
        }

        return UserLoginResponse.builder()
                .token(jwtTokenUtil.createToken(dto.getUserName()))
                .build();
    }

    public User getUsername(String userName) {
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()-> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s가 존재하지 않습니다.", userName)));
        return user;
    }
}
