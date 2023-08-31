package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.model.dto.PostDetailDto;
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
    private List<String> tags;
    private List<String> resources;
    private LocalDateTime createdDate;
    private LocalDateTime popularityDate;
    private long ACount;
    private long BCount;
    private long likeCount;
    private long commentCount;
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
                .tags(post.getTags())
                .ACount(post.getACount())
                .BCount(post.getBCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .createdDate(post.getCreatedDate())
                .popularityDate(post.getPopularityDate())
                .build();
    }

    public static PostDetailRes of(PostDetailDto dto) {
        return PostDetailRes.builder()
                .postId(dto.getId())
                .member(new MemberRes(dto.getMemberId(), dto.getNickname()))
                .title(dto.getTitle())
                .content(dto.getContent())
                .optionA(dto.getOptionA())
                .optionB(dto.getOptionB())
                .tags(dto.getTags())
                .resources(dto.getResources())
                .ACount(dto.getACount())
                .BCount(dto.getBCount())
                .likeCount(dto.getLikeCount())
                .commentCount(dto.getCommentCount())
                .createdDate(dto.getCreatedDate())
                .popularityDate(dto.getPopularityDate())
                .build();
    }
}
