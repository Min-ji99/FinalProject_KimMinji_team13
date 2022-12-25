package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.PostDto;
import com.likelion.sns.domain.dto.PostWriteRequest;
import com.likelion.sns.domain.dto.PostWriteResponse;
import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.service.PostService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.SortDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

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
        PostWriteResponse postWriteResponse=postService.write(dto, authentication.getName());
        return Response.success(postWriteResponse);
    }
    @GetMapping
    public Response<Page<PostDto>> list(@PageableDefault(size=20)
                                            @SortDefault(sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable){
        Page<PostDto> posts=postService.getList(pageable);
        return Response.success(posts);
    }
}
