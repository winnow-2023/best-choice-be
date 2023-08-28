package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@Builder
public class PostRes {

    private long postId;
    private MemberRes member;
    private String title;
    private String optionA;
    private String optionB;
    private List<String> tags;
    private LocalDateTime createdDate;
    private LocalDateTime popularityDate;
    private long likeCount;
    private long choiceCount;
    private long commentCount;
    private boolean chattingActive;

    public static PostRes of(Post post) {
        return PostRes.builder()
                .postId(post.getId())
                .member(MemberRes.of(post.getMember()))
                .title(post.getTitle())
                .optionA(post.getOptionA())
                .optionB(post.getOptionB())
                .tags(post.getTags())
                .createdDate(post.getCreatedDate())
                .popularityDate(post.getPopularityDate())
                .likeCount(post.getLikeCount())
                .choiceCount(post.getACount() + post.getBCount())
                .commentCount(post.getCommentCount())
                .build();
    }
}
