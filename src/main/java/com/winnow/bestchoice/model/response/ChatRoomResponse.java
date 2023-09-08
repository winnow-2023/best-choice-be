package com.winnow.bestchoice.model.response;

import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@AllArgsConstructor
@Builder
public class ChatRoomResponse {
    private String title;
    private String optionA;
    private String optionB;
    private long likeCount;
    private long commentCount;
    private String nickname;
    private LocalDateTime createdDate;
    private long userCount;

    public ChatRoomResponse(String title, String optionA, String optionB, long likeCount, long commentCount, String nickname, LocalDateTime createdDate) {
        this.title = title;
        this.optionA = optionA;
        this.optionB = optionB;
        this.likeCount = likeCount;
        this.commentCount = commentCount;
        this.nickname = nickname;
        this.createdDate = createdDate;
    }

}
