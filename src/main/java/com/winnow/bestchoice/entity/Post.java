package com.winnow.bestchoice.entity;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Builder
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Column(nullable = false, name = "title")
    private String title;

    @Column(nullable = false, name = "content")
    private String content;

    @Column(nullable = false, name = "optionA")
    private String optionA;

    @Column(nullable = false, name = "optionB")
    private String optionB;

    @Column(name = "ACount")
    private long ACount;

    @Column(name = "BCount")
    private long BCount;

    @Column(name = "like_count")
    private long likeCount;

    @CreatedDate
    @Column(nullable = false, name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "popularity_date")
    private LocalDateTime popularityDate;

    @Column(name = "deleted")
    private boolean deleted;
}
