package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.model.dto.PostSummaryDto;
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

    public static PostRes of(PostSummaryDto dto) {
        return PostRes.builder()
                .postId(dto.getId())
                .member(new MemberRes(dto.getMemberId(), dto.getNickname()))
                .title(dto.getTitle())
                .optionA(dto.getOptionA())
                .optionB(dto.getOptionB())
                .tags(dto.getTags())
                .createdDate(dto.getCreatedDate())
                .popularityDate(dto.getPopularityDate())
                .likeCount(dto.getLikeCount())
                .choiceCount(dto.getACount() + dto.getBCount())
                .commentCount(dto.getCommentCount())
                .build();
    }
}
