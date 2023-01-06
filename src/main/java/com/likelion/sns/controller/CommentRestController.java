package com.likelion.sns.controller;

import com.likelion.sns.domain.dto.Response;
import com.likelion.sns.domain.dto.comment.CommentDeleteResponse;
import com.likelion.sns.domain.dto.comment.CommentDto;
import com.likelion.sns.domain.dto.comment.CommentModifyRequest;
import com.likelion.sns.domain.dto.comment.CommentWriteRequest;
import com.likelion.sns.service.CommentService;
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
public class CommentRestController {
    private final CommentService commentService;

    public CommentRestController(CommentService commentService) {
        this.commentService = commentService;
    }

    @PostMapping("/{postId}/comments")
    public Response<CommentDto> writeComment(@PathVariable Integer postId, @ApiIgnore Authentication authentication, @RequestBody CommentWriteRequest dto){
        CommentDto commentDto =commentService.writeComment(postId, dto, authentication.getName());
        return Response.success(commentDto);
    }
    @PutMapping("/{postId}/comments/{id}")
    public Response<CommentDto> modifyComment(@PathVariable Long id, @ApiIgnore Authentication authentication, @RequestBody CommentModifyRequest dto){
        CommentDto commentDto =commentService.modifyComment(id, dto, authentication.getName());
        return Response.success(commentDto);
    }
    @DeleteMapping("/{postId}/comments/{id}")
    public Response<CommentDeleteResponse> deleteComment(@PathVariable Long id, @ApiIgnore Authentication authentication){
        CommentDeleteResponse commentDeleteResponse =commentService.deleteComment(id, authentication.getName());
        return Response.success(commentDeleteResponse);
    }
    @GetMapping("/{postId}/comments")
    public Response<Page<CommentDto>> getCommentList(@PageableDefault(size=10)
                                                     @SortDefault(sort="createdAt", direction= Sort.Direction.DESC) Pageable pageable, @PathVariable Integer postId){
        Page<CommentDto> commentDtos=commentService.getCommentList(postId, pageable);
        return Response.success(commentDtos);
    }
}
