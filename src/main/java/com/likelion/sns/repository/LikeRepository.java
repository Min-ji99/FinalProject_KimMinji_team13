package com.likelion.sns.repository;

import com.likelion.sns.domain.entity.Like;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Integer> {
    Optional<Like> findByPostAndUser(Post post, User user);
    Long countByPost(Post post);
}
