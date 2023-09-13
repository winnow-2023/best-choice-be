package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.model.dto.ChatMessage;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {

    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /**
     *  /pub/chat/message로 들어오는 메세징을 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message) {
        log.info("[ChatController.message 호출] roomId : {}, message : {}", message.getRoomId(), message);

        String nickname = message.getSender();
        message.setSender(nickname);
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));
        message.setSendTime(LocalDateTime.now());

        chatService.sendChatMessage(message);
    }
}
