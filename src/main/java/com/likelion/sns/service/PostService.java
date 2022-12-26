package com.likelion.sns.service;

import com.likelion.sns.domain.dto.PostDto;
import com.likelion.sns.domain.dto.PostModifyRequet;
import com.likelion.sns.domain.dto.PostWriteRequest;
import com.likelion.sns.domain.dto.PostWriteResponse;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.PostRepository;
import com.likelion.sns.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    public PostService(PostRepository postRepository, UserRepository userRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
    }

    public PostWriteResponse write(PostWriteRequest dto, String userName) {
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        Post post=postRepository.save(dto.toEntity(user));
        return PostWriteResponse.builder()
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

    public PostWriteResponse modify(Integer id, PostModifyRequet dto, String userName) {
        Post post=postRepository.findById(id)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));

        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));
        if(user.getId()!=post.getUser().getId()){
            throw new AppException(ErrorCode.INVALID_PERMISSION, String.format("user %s는 해당 포스트 접근 권한이 없습니다.", user.getUserName()));
        }
        post.setTitle(dto.getTitle());
        post.setBody(dto.getBody());
        Post savedPost=postRepository.save(post);

        return PostWriteResponse.builder()
                .message("포스트 수정 완료")
                .postId(post.getId())
                .build();
    }
}
