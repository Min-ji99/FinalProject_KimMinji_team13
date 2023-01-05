package com.likelion.sns.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@SQLDelete(sql="UPDATE post SET deleted_at = current_timestamp WHERE id = ?")
@Where(clause="deleted_at is NULL")
public class Post extends BaseEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String title;
    private String body;

    @ManyToOne
    @JoinColumn(name="user_id")
    private User user;

    @Column(name="deleted_at")
    private LocalDateTime deletedAt;

    public void updatePost(String title, String body){
        this.title=title;
        this.body=body;
    }
}
