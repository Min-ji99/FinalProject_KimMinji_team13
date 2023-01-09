package com.likelion.sns.service;

import com.likelion.sns.domain.dto.user.*;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.enums.UserRole;
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
        validateDuplicateUser(dto.getUserName());

        User user=userRepository.save(dto.toEntity(encoder.encode(dto.getPassword())));
        return UserJoinResponse.builder()
                .userId(user.getId())
                .userName(user.getUserName())
                .build();
    }

    public UserLoginResponse login(UserLoginRequest dto) {
        //존재하는 유저인지 확인
        User user=getUserEntityByUsername(dto.getUserName());

        //password가 일치하는지 확인
        if(!encoder.matches(dto.getPassword(), user.getPassword())){
            throw new AppException(ErrorCode.INVALID_PASSWORD, String.format("Username 또는 password가 잘못되었습니다."));
        }

        return UserLoginResponse.builder()
                .jwt(jwtTokenUtil.createToken(dto.getUserName()))
                .build();
    }

    public User getUsername(String userName) {
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()-> new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s가 존재하지 않습니다.", userName)));
        return user;
    }

    public UserRoleChangeResponse changeRole(UserRoleChangeRequest dto, Integer id, String adminUserName) {
        //admin 사용자가 존재하는지 확인
        User admin=getUserEntityByUsername(adminUserName);
        //권한을 변경시킬 사용자가 존재하는지 확인
        User user=userRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("userId %d이 존재하지 않습니다.", id)));

        user.setRole(UserRole.of(dto.getRole()));
        User savedUser=userRepository.save(user);
        return UserRoleChangeResponse.builder()
                .userId(savedUser.getId())
                .userName(savedUser.getUserName())
                .role(savedUser.getRole())
                .build();
    }
    public User getUserEntityByUsername(String userName){
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("%s이 존재하지 않습니다.", userName)));
        return user;
    }
    public boolean validateDuplicateUser(String userName){
        //userName이 중복되는지 확인
        userRepository.findByUserName(userName)
                .ifPresent(user->{
                    throw new AppException(ErrorCode.DUPLICATED_USER_NAME, String.format("%s는 존재하는 이름입니다.", userName));
                });
        return true;
    }
}
