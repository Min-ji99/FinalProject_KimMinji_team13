package com.likelion.sns.service;

import com.likelion.sns.domain.dto.*;
import com.likelion.sns.domain.entity.Comment;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.enums.UserRole;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.CommentRepository;
import com.likelion.sns.repository.PostRepository;
import com.likelion.sns.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
    }

    public PostResponse writePost(PostWriteRequest dto, String userName) {
        //userName이 존재하지 않으면 USERNAME_NOT_FOUND 예외 발생
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        Post post=postRepository.save(dto.toEntity(user));
        return PostResponse.builder()
                .message("포스트 등록 완료")
                .postId(post.getId())
                .build();
    }

    public Page<PostDto> getPostlist(Pageable pageable) {
        Page<Post> posts=postRepository.findAll(pageable);
        Page<PostDto> postResponses= PostDto.toList(posts);

        return postResponses;
    }

    public PostDto findPostById(Integer id) {
        //postId가 존재하지 않으면 POST_NOT_FOUND 예외발생
        Post post=postRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));
        return PostDto.builder()
                .id(post.getId())
                .title(post.getTitle())
                .body(post.getBody())
                .userName(post.getUser().getUserName())
                .createdAt(post.getCreatedAt())
                .lastModifiedAt(post.getLastModifiedAt())
                .build();
    }

    @Transactional
    public PostResponse modifyPost(Integer postId, PostModifyRequet dto, String userName) {
        Post post=getPostEntity(postId, userName);

        post.updatePost(dto.getTitle(), dto.getBody());

        return PostResponse.builder()
                .message("포스트 수정 완료")
                .postId(post.getId())
                .build();
    }

    public PostResponse deletePost(Integer postId, String userName) {
        Post post=getPostEntity(postId, userName);

        postRepository.deleteById(postId);
        return PostResponse.builder()
                .message("포스트 삭제 완료")
                .postId(post.getId())
                .build();
    }

    public CommentResponse writeComment(Integer id, CommentWriteRequest dto, String userName) {
        //존재하는 post인지 확인
        Post post=postRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));

        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));
        Comment comment=commentRepository.save(dto.toEntity(user, post));

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .message("댓글 등록 완료")
                .build();
    }

    @Transactional
    public CommentResponse modifyComment(Integer commentId, CommentModifyRequest dto, String userName) {
        Comment comment=getCommentEntity(commentId, userName);

        comment.updateComment(dto.getComment());

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .message("댓글 수정 완료")
                .build();
    }

    public CommentResponse deleteComment(Integer commentId, String userName) {
        Comment comment=getCommentEntity(commentId, userName);

        commentRepository.deleteById(commentId);
        return CommentResponse.builder()
                .message("댓글 삭제 완료")
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .build();
    }
    private Post getPostEntity(Integer postId, String userName){
        //존재하는 Post인지 확인
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));
        validatePostPermission(post, userName);

        return post;
    }
    private boolean validatePostPermission(Post post, String userName){
        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));
        //post 작성자와 유저가 일치하는지 확인
        //현재 유저의 권한이 ADMIN이 아닌지 확인
        if(user.getId()!=post.getUser().getId() && user.getRole()!=UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 포스트 접근 권한이 없습니다.", user.getUserName()));
        }
        return true;
    }
    private Comment getCommentEntity(Integer commentId, String userName){
        //존재하는 comment인지 확인
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new AppException(ErrorCode.COMMENT_NOT_FOUND, String.format("해당 comment가 존재하지 않습니다.")));

        validateCommentPermission(comment, userName);
        return comment;
    }
    private boolean validateCommentPermission(Comment comment, String userName){
        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        //댓글 작성자와 유저가 일치하는지 확인
        //현재 유저의 권한이 ADMIN이 아닌지 확인
        if(user.getId()!=comment.getUser().getId() && user.getRole() != UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 comment 접근 권한이 없습니다.", user.getUserName()));
        }
        return true;
    }
}
