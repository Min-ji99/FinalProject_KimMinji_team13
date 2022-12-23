package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.PostWriteRequest;
import com.likelion.sns.domain.dto.PostWriteResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.neo4j.Neo4jProperties;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostRestController {
    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Response<PostWriteResponse> write(@RequestBody PostWriteRequest dto, Authentication authentication){
        log.info("controller : {}", authentication);
        if(!authentication.isAuthenticated()){
            throw new AppException(ErrorCode.INVALID_PERMISSION, "로그인을 먼저 진행해야 합니다.");
        }
        PostWriteResponse postWriteResponse=postService.write(dto, authentication.getName());
        return Response.success(postWriteResponse);
    }
}
