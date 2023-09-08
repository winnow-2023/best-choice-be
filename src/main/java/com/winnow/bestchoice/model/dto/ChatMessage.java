package com.winnow.bestchoice.model.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class ChatMessage {
    // 메세지 타입 : 입장, 퇴장, 채팅
    public enum MessageType {
        ENTER, QUIT, TALK
    }

    private MessageType type;
    private String roomId;
    private String sender;
    private String message;
    private long userCount;
}
