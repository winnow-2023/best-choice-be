package com.winnow.bestchoice.model.dto;

import com.winnow.bestchoice.type.Option;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class CommentDto {
    private long id;
    private long memberId;
    private String nickname;
    private Option option;
    private String content;
    private long likeCount;
    private boolean liked;
    private LocalDateTime createdDate;
    private LocalDateTime deletedDate;
}
