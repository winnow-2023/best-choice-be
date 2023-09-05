package com.winnow.bestchoice.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@Table(name = "ATTACHMENT")
public class Attachment {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "post_id")
    private Post post;

    @Column(nullable = false, name = "url")
    private String url;

    public Attachment(Post post, String url) {
        this.post = post;
        this.url = url;
    }
}
