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
import springfox.documentation.annotations.ApiIgnore;

@RestController
@RequestMapping("/api/v1/posts")
@Slf4j
public class PostRestController {
    private final PostService postService;

    public PostRestController(PostService postService) {
        this.postService = postService;
    }

    @PostMapping
    public Response<PostResponse> write(@RequestBody PostWriteRequest dto, @ApiIgnore Authentication authentication){
        log.info("controller : {}", authentication);
        PostResponse postResponse =postService.writePost(dto, authentication.getName());
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
    public Response<PostResponse> modify(@PathVariable Integer id, @RequestBody PostModifyRequet dto, @ApiIgnore Authentication authentication){
        PostResponse postResponse =postService.modifyPost(id, dto, authentication.getName());
        return Response.success(postResponse);
    }
    @DeleteMapping("/{id}")
    public Response<PostResponse> delete(@PathVariable Integer id, @ApiIgnore Authentication authentication){
        PostResponse postResponse=postService.deletePost(id, authentication.getName());
        return Response.success(postResponse);
    }
    @PostMapping("/{postId}/comments")
    public Response<CommentDto> writeComment(@PathVariable Integer postId, @ApiIgnore Authentication authentication, @RequestBody CommentWriteRequest dto){
        CommentDto commentDto =postService.writeComment(postId, dto, authentication.getName());
        return Response.success(commentDto);
    }
    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentDto> modifyComment(@PathVariable Long id, @ApiIgnore Authentication authentication, @RequestBody CommentModifyRequest dto){
        CommentDto commentDto =postService.modifyComment(id, dto, authentication.getName());
        return Response.success(commentDto);
    }
    @DeleteMapping("/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Long id, @ApiIgnore Authentication authentication){
        CommentDeleteResponse commentDeleteResponse =postService.deleteComment(id, authentication.getName());
        return Response.success(commentDeleteResponse);
    }
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentDto>> getCommentList(@PageableDefault(size=10)
                                               @SortDefault(sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable, @PathVariable Integer postId){
        Page<CommentDto> commentDtos=postService.getCommentList(postId, pageable);
        return Response.success(commentDtos);
    }
    @PostMapping("/{postId}/likes")
    public Response<String> like(@PathVariable Integer postId, Authentication authentication){
        String likeResponse=postService.like(postId, authentication.getName());
        return Response.success(likeResponse);
    }
    @GetMapping("/{postId}/likes")
    public Response<Long> likeCount(@PathVariable Integer postId){
        Long likeCount=postService.likeCount(postId);

        return Response.success(likeCount);
    }
    @GetMapping("/my")
    public Response<Page<PostDto>> myFeed(@PageableDefault(size=20)
                                              @SortDefault(sort="createdAt", direction=Sort.Direction.DESC) Pageable pageable,
                                          @ApiIgnore Authentication authentication){
        Page<PostDto> feed=postService.getMyFeed(authentication.getName(), pageable);
        return Response.success(feed);
    }
}
