package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.domain.dto.UserJoinRequest;
import com.likelion.sns.domain.dto.UserJoinResponse;
import com.likelion.sns.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/users")
public class UserRestController {
    private final UserService userService;

    public UserRestController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/join")
    public Response<UserJoinResponse> join(@RequestBody UserJoinRequest dto){
        UserJoinResponse userJoinResponse=userService.join(dto);
        return Response.success(userJoinResponse);
    }
}
