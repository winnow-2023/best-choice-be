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
    private long memberId;
    private String nickname;
    private String title;
    private String content;
    private String optionA;
    private String optionB;
    private List<String> resources;
    private List<String> tags;
    private LocalDateTime createdDate;
    private LocalDateTime popularityDate;
    private long likeCount;
    private int ACount;
    private int BCount;
    private String liveChatUrl;
    private String liveChatUserCount;

    public static PostDetailRes of(Post post) {
        return PostDetailRes.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .optionA(post.getOptionA())
                .optionB(post.getOptionB())
                .createdDate(post.getCreatedDate())
                .build();
    }
}
