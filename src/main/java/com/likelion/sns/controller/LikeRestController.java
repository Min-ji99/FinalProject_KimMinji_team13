package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.service.LikeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
@Api(tags="Like")
public class LikeRestController {
    private final LikeService likeService;

    public LikeRestController(LikeService likeService) {
        this.likeService = likeService;
    }

    @ApiOperation(value="좋아요 누르기", notes="포스트에 좋아요 누르는 기능. 한번만 가능")
    @PostMapping("/{postId}/likes")
    public Response<String> like(@PathVariable Integer postId, Authentication authentication){
        String likeResponse=likeService.like(postId, authentication.getName());
        return Response.success(likeResponse);
    }
    @ApiOperation(value="좋아요 조회", notes = "포스트의 좋아요 갯수 조회")
    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId){
        Long likeCount=likeService.likeCount(postId);

        return Response.success(likeCount);
    }
}
