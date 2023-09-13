package com.winnow.bestchoice.entity;

import com.winnow.bestchoice.config.converter.TagsToJsonConverter;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(name = "tags")
    @Convert(converter = TagsToJsonConverter.class)
    private List<String> tags;

    @Column(name = "ACount")
    private long ACount;

    @Column(name = "BCount")
    private long BCount;

    @Column(name = "like_count")
    private long likeCount;

    @Column(name = "comment_count")
    private long commentCount;

    @CreatedDate
    @Column(nullable = false, name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "popularity_date")
    private LocalDateTime popularityDate;

    @Column(name = "deleted")
    private boolean deleted;

    @Column(name = "liveChatActive")
    private boolean liveChatActive;

    public Post(Long id) {this.id = id;}

}
