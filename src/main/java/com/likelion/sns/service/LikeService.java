package com.likelion.sns.service;

import com.likelion.sns.domain.entity.Like;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import com.likelion.sns.enums.ErrorCode;
import com.likelion.sns.exception.AppException;
import com.likelion.sns.repository.LikeRepository;
import com.likelion.sns.repository.PostRepository;
import com.likelion.sns.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LikeService {
    private static final String LIKE_SUCCESS="좋아요를 눌렀습니다";
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final LikeRepository likeRepository;

    public LikeService(PostRepository postRepository, UserRepository userRepository, LikeRepository likeRepository) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.likeRepository = likeRepository;
    }

    @Transactional
    public String like(Integer postId, String userName) {
        Post post=getPostEntity(postId);
        User user=getUserEntity(userName);
        likeRepository.findByPostAndUser(post, user)
                .ifPresent(like->{
                    throw new AppException(ErrorCode.DUPLICATED_LIKE, ErrorCode.DUPLICATED_LIKE.getMessage());
                });
        likeRepository.save(Like.of(post, user));
        return LIKE_SUCCESS;
    }
    public Long likeCount(Integer postId) {
        //post 존재하는지 확인
        Post post=getPostEntity(postId);
        //Like가 없다면 0으로 반환
        Long likeCnt=likeRepository.countByPost(post);
        return likeCnt;
    }
    private Post getPostEntity(Integer postId){
        //존재하는 Post인지 확인
        Post post=postRepository.findById(postId)
                .orElseThrow(()->new AppException(ErrorCode.POST_NOT_FOUND, String.format("해당 포스트가 존재하지 않습니다.")));

        return post;
    }
    private User getUserEntity(String userName){
        //존재하는 유저인지 확인
        User user=userRepository.findByUserName(userName)
                .orElseThrow(()->new AppException(ErrorCode.USERNAME_NOT_FOUND, String.format("username %s이 존재하지 않습니다.", userName)));

        return user;
    }
}
