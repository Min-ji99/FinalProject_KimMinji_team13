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

    public PostResponse write(PostWriteRequest dto, String userName) {
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

    public PostResponse modify(Integer id, PostModifyRequet dto, String userName) {
        //존재하는 Post인지 확인
        Post post=postRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));

        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        //post 작성자와 유저가 일치하는지 확인
        //현재 유저의 권한이 ADMIN이 아닌지 확인
        if(user.getId()!=post.getUser().getId() && user.getRole() != UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 포스트 접근 권한이 없습니다.", user.getUserName()));
        }
        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());
        Post savedPost=postRepository.save(post);

        return PostResponse.builder()
                .message("포스트 수정 완료")
                .postId(savedPost.getId())
                .build();
    }

    public PostResponse delete(Integer id, String userName) {
        //존재하는 Post인지 확인
        Post post=postRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));

        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));
        //post 작성자와 유저가 일치하는지 확인
        //현재 유저의 권한이 ADMIN이 아닌지 확인
        if(user.getId()!=post.getUser().getId() && user.getRole()!=UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 포스트 접근 권한이 없습니다.", user.getUserName()));
        }
        postRepository.deleteById(id);
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

    public CommentResponse modifyComment(Integer id, CommentModifyRequest dto, String userName) {
        //존재하는 Post인지 확인
        Comment comment=commentRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.COMMENT_NOT_FOUND, String.format("해당 comment가 존재하지 않습니다.")));

        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        //post 작성자와 유저가 일치하는지 확인
        //현재 유저의 권한이 ADMIN이 아닌지 확인
        if(user.getId()!=comment.getUser().getId() && user.getRole() != UserRole.ADMIN){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 포스트 접근 권한이 없습니다.", user.getUserName()));
        }
        comment.setComment(dto.getComment());
        Comment savedComment=commentRepository.save(comment);

        return CommentResponse.builder()
                .id(comment.getId())
                .postId(savedComment.getPost().getId())
                .userName(savedComment.getUser().getUserName())
                .message("댓글 수정 완료")
                .build();
    }
}
