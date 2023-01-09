package com.likelion.sns.repository;

import com.likelion.sns.domain.entity.Alarm;
import com.likelion.sns.domain.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlarmRepository extends JpaRepository<Alarm, Integer> {
    Page<Alarm> findAllByUser(User user, Pageable pageable);
}
