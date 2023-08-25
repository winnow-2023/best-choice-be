package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Comment;
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
//    private Option option; todo 조회 구현
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;

    public static CommentRes of(Comment comment) {
        return CommentRes.builder()
                .commentId(comment.getId())
                .member(MemberRes.of(comment.getMember()))
                .content(comment.getContent())
                .likeCount(comment.getLikeCount())
                .createdDate(comment.getCreatedDate())
                .deletedDate(comment.getDeletedDate())
                .build();
    }
}
