package com.likelion.sns.service;

import com.likelion.sns.domain.dto.comment.CommentDeleteResponse;
import com.likelion.sns.domain.dto.comment.CommentDto;
import com.likelion.sns.domain.dto.comment.CommentModifyRequest;
import com.likelion.sns.domain.dto.comment.CommentWriteRequest;
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
public class CommentService {
    private static final String COMMENT_DELETE_SUCCESS="댓글 삭제 완료";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    public CommentService(PostRepository postRepository, UserRepository userRepository, CommentRepository commentRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.commentRepository = commentRepository;
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
    private boolean matchWriterAndComment(Comment comment, User user){
        if(user.getId()!=comment.getUser().getId() && user.getRole() != UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 comment 접근 권한이 없습니다.", user.getUserName()));
        }
        return true;
    }
}
