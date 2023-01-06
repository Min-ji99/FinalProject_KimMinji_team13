package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.service.LikeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
public class LikeRestController {
    private final LikeService likeService;

    public LikeRestController(LikeService likeService) {
        this.likeService = likeService;
    }

    @PostMapping("/{postId}/likes")
    public Response<String> like(@PathVariable Integer postId, Authentication authentication){
        String likeResponse=likeService.like(postId, authentication.getName());
        return Response.success(likeResponse);
    }
    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId){
        Long likeCount=likeService.likeCount(postId);

        return Response.success(likeCount);
    }
}
