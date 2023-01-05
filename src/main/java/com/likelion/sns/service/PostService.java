package com.likelion.sns.service;

import com.likelion.sns.domain.dto.*;
import com.likelion.sns.domain.entity.Comment;
import com.likelion.sns.domain.entity.Like;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.enums.UserRole;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.CommentRepository;
import com.likelion.sns.repository.LikeRepository;
import com.likelion.sns.repository.PostRepository;
import com.likelion.sns.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class PostService {
    private static final String POST_WRITE_SUCCESS="포스트 등록 완료";
    private static final String POST_MODIFY_SUCCESS="포스트 수정 완료";
    private static final String POST_DELETE_SUCCESS="포스트 삭제 완료";
    private static final String COMMENT_DELETE_SUCCESS="댓글 삭제 완료";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
        this.likeRepository = likeRepository;
    }

    public PostResponse writePost(PostWriteRequest dto, String userName) {
        //userName이 존재하지 않으면 USERNAME_NOT_FOUND 예외 발생
        User user=getUserEntity(userName);

        Post post=postRepository.save(dto.toEntity(user));
        return PostResponse.builder()
                .message(POST_WRITE_SUCCESS)
                .postId(post.getId())
                .build();
    }

    public Page<PostDto> getPostlist(Pageable pageable) {
        Page<Post> posts=postRepository.findAll(pageable);
        Page<PostDto> postResponses= PostDto.toList(posts);

        return postResponses;
    }

    public PostDto findPostById(Integer postId) {
        //postId가 존재하지 않으면 POST_NOT_FOUND 예외발생
        Post post=getPostEntity(postId);
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
        Post post=getPostEntity(postId);
        matchWriterAndPost(post, getUserEntity(userName));

        post.updatePost(dto.getTitle(), dto.getBody());

        return PostResponse.builder()
                .message(POST_MODIFY_SUCCESS)
                .postId(post.getId())
                .build();
    }

    public PostResponse deletePost(Integer postId, String userName) {
        Post post=getPostEntity(postId);
        matchWriterAndPost(post, getUserEntity(userName));
        postRepository.deleteById(postId);
        return PostResponse.builder()
                .message(POST_DELETE_SUCCESS)
                .postId(post.getId())
                .build();
    }
    public Page<CommentDto> getCommentList(Integer postId, Pageable pageable) {
        Post post=getPostEntity(postId);

        Page<Comment> comments=commentRepository.findByPost(pageable, post);
        Page<CommentDto> commentDtos=CommentDto.toList(comments);
        return commentDtos;
    }

    public CommentDto writeComment(Integer postId, CommentWriteRequest dto, String userName) {
        Post post=getPostEntity(postId);
        User user=getUserEntity(userName);

        Comment comment=commentRepository.save(dto.toEntity(user, post));

        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .build();
    }

    @Transactional
    public CommentDto modifyComment(Long commentId, CommentModifyRequest dto, String userName) {
        Comment comment=getCommentEntity(commentId);

        matchWriterAndComment(comment, getUserEntity(userName));

        comment.updateComment(dto.getComment());

        return CommentDto.builder()
                .id(comment.getId())
                .postId(comment.getPost().getId())
                .userName(comment.getUser().getUserName())
                .comment(comment.getComment())
                .createdAt(comment.getCreatedAt())
                .lastModifiedAt(comment.getLastModifiedAt())
                .build();
    }

    public CommentDeleteResponse deleteComment(Long commentId, String userName) {
        Comment comment=getCommentEntity(commentId);
        matchWriterAndComment(comment, getUserEntity(userName));
        commentRepository.deleteById(commentId);
        return CommentDeleteResponse.builder()
                .message(COMMENT_DELETE_SUCCESS)
                .id(comment.getId())
                .build();
    }
    public LikeResponse like(Integer postId, String userName) {
        Post post=getPostEntity(postId);
        User user=getUserEntity(userName);
        likeRepository.findByPostAndUser(post, user)
                .ifPresent(like->{
                    throw new AppException(ErrorCode.DUPLICATED_LIKE, ErrorCode.DUPLICATED_LIKE.getMessage());
                });
        likeRepository.save(Like.of(post, user));
        return LikeResponse.builder()
                .message("좋아요를 눌렀습니다.")
                .build();
    }
    private Post getPostEntity(Integer postId){
        //존재하는 Post인지 확인
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));

        return post;
    }
    private Comment getCommentEntity(Long commentId){
        //존재하는 Post인지 확인
        Comment comment=commentRepository.findById(commentId)
                .orElseThrow(()->new AppException(ErrorCode.COMMENT_NOT_FOUND, String.format("해당 댓글이 존재하지 않습니다.")));

        return comment;
    }
    private User getUserEntity(String userName){
        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        return user;
    }
    private boolean matchWriterAndPost(Post post, User user){
        if(user.getId()!=post.getUser().getId() && user.getRole() != UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 post 접근 권한이 없습니다.", user.getUserName()));
        }
        return true;
    }
    private boolean matchWriterAndComment(Comment comment, User user){
        if(user.getId()!=comment.getUser().getId() && user.getRole() != UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 comment 접근 권한이 없습니다.", user.getUserName()));
        }
        return true;
    }
}
