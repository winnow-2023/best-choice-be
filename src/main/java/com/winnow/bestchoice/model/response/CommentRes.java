package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Comment;
import com.winnow.bestchoice.model.dto.CommentDto;
import com.winnow.bestchoice.type.Option;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class CommentRes {

    private long commentId;
    private MemberRes member;
    private String content;
    private long likeCount;
    private Option option;
    private boolean liked;
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;

    public static CommentRes of(CommentDto dto) {
        return CommentRes.builder()
                .commentId(dto.getId())
                .member(new MemberRes(dto.getMemberId(), dto.getNickname()))
                .content(dto.getContent())
                .likeCount(dto.getLikeCount())
                .option(dto.getOption())
                .liked(dto.isLiked())
                .createdDate(dto.getCreatedDate())
                .deletedDate(dto.getDeletedDate())
                .build();
    }
}
