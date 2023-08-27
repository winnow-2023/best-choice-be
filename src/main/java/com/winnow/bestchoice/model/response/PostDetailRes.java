package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class PostDetailRes {

    private long postId;
    private MemberRes member;
    private String title;
    private String content;
    private String optionA;
    private String optionB;
    private List<String> resources;
    private List<String> tags;
    private LocalDateTime createdDate;
    private LocalDateTime popularityDate;
    private long likeCount;
    private long ACount;
    private long BCount;
    private String liveChatUrl;
    private String liveChatUserCount;

    public static PostDetailRes of(Post post) {
        return PostDetailRes.builder()
                .postId(post.getId())
                .member(MemberRes.of(post.getMember()))
                .title(post.getTitle())
                .content(post.getContent())
                .optionA(post.getOptionA())
                .optionB(post.getOptionB())
                .ACount(post.getACount())
                .BCount(post.getBCount())
                .createdDate(post.getCreatedDate())
                .popularityDate(post.getPopularityDate())
                .build();
    }
}
