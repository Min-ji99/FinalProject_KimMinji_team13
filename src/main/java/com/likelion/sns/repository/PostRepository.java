package com.likelion.sns.repository;

import com.likelion.sns.domain.dto.PostDto;
import com.likelion.sns.domain.entity.Post;
import com.likelion.sns.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PostRepository extends JpaRepository<Post, Integer> {
    Page<Post> findAll(Pageable pageable);

    Page<Post> findByUser(User user, Pageable pageable);
}
