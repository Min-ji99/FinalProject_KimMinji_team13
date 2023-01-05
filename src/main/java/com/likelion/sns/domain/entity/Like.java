package com.likelion.sns.domain.entity;

import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@Table(name="likes")
@SQLDelete(sql="UPDATE likes SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause="deleted_at is NULL")
public class Like extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name="post_id")
    private Post post;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public static Like of(Post post, User user) {
        return Like.builder()
                .post(post)
                .user(user)
                .build();
    }
}
