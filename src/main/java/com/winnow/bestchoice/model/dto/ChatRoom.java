package com.winnow.bestchoice.model.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Getter
@Setter
@ToString
public class ChatRoom implements Serializable {

    private static final long serialVersionUID = 6494678977089006639L;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy.MM.dd HH:mm:ss");
    private String roomId;
    private long userCount; // 채팅방 인원수
    private String createdDate;

    public static ChatRoom create(String roomId) {
        ChatRoom chatRoom = new ChatRoom();
        chatRoom.roomId = roomId;
        chatRoom.userCount = 0;
        chatRoom.createdDate = LocalDateTime.now().format(FORMATTER);

        return chatRoom;
    }

}
