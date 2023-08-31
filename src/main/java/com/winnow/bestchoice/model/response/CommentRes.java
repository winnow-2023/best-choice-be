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
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;

//    public static CommentRes of(Comment comment) {
//        return CommentRes.builder()
//                .commentId(comment.getId())
//                .member(MemberRes.of(comment.getMember()))
//                .content(comment.getContent())
//                .likeCount(comment.getLikeCount())
//                .createdDate(comment.getCreatedDate())
//                .deletedDate(comment.getDeletedDate())
//                .build();
//    }

    public static CommentRes of(CommentDto dto) {
        return CommentRes.builder()
                .commentId(dto.getId())
                .member(new MemberRes(dto.getMemberId(), dto.getNickname()))
                .content(dto.getContent())
                .likeCount(dto.getLikeCount())
                .option(dto.getOption())
                .createdDate(dto.getCreatedDate())
                .deletedDate(dto.getDeletedDate())
                .build();
    }
}
