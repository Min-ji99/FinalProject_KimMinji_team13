package com.likelion.sns.repository;

import com.likelion.sns.domain.entity.Comment;
import com.likelion.sns.domain.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    Page<Comment> findByPost(Pageable pageable, Post post);
}
