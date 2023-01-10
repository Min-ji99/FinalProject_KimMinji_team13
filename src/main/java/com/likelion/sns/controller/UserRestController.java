package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.*;
import com.likelion.sns.domain.dto.user.*;
import com.likelion.sns.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@Api(tags="User")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @ApiOperation(value = "회원가입", notes="username 중복 불가능")
    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest dto){
        UserJoinResponse userJoinResponse=userService.join(dto);
        return Response.success(userJoinResponse);
    }
    @ApiOperation(value="로그인", notes="로그인 성공 시 토큰 발급")
    @PostMapping("/login")
    public Response<UserLoginResponse> login(@RequestBody UserLoginRequest dto){
        UserLoginResponse userLoginResponse=userService.login(dto);
        return Response.success(userLoginResponse);
    }

    @ApiOperation(value="권한 변경", notes="ADMIN 권한을 갖고 있는 사용자만 권한 변경 가능\n 권한은 admin 또는 user")
    @PostMapping("/{id}/role/change")
    public Response<UserRoleChangeResponse> changeRole(@PathVariable Integer id, @RequestBody UserRoleChangeRequest dto, Authentication authentication){
        UserRoleChangeResponse userRoleChangeResponse=userService.changeRole(dto, id, authentication.getName());
        return Response.success(userRoleChangeResponse);
    }
}
