package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.*;
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
    public Response<PostResponse> write(@RequestBody PostWriteRequest dto, Authentication authentication){
        log.info("controller : {}", authentication);
        PostResponse postResponse =postService.write(dto, authentication.getName());
        return Response.success(postResponse);
    }
    @GetMapping
    public Response<Page<PostDto>> getPostlist(@PageableDefault(size=20)
                                            @SortDefault(sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable){
        Page<PostDto> posts=postService.getPostlist(pageable);
        return Response.success(posts);
    }
    @GetMapping("/{postId}")
    public Response<PostDto> findPostById(@PathVariable Integer postId){
        PostDto post=postService.findPostById(postId);
        return Response.success(post);
    }
    @PutMapping("/{id}")
    public Response<PostResponse> modify(@PathVariable Integer id, @RequestBody PostModifyRequet dto, Authentication authentication){
        PostResponse postResponse =postService.modify(id, dto, authentication.getName());
        return Response.success(postResponse);
    }
    @DeleteMapping("/{id}")
    public Response<PostResponse> delete(@PathVariable Integer id, Authentication authentication){
        PostResponse postResponse=postService.delete(id, authentication.getName());
        return Response.success(postResponse);
    }
    @PostMapping("/{id}/comments")
    public Response<CommentResponse> writeComment(@PathVariable Integer id, Authentication authentication, @RequestBody CommentWriteRequest dto){
        CommentResponse commentResponse=postService.writeComment(id, dto, authentication.getName());
        return Response.success(commentResponse);
    }
}
