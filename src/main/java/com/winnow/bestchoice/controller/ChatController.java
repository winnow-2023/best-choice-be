package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.model.dto.ChatMessage;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ChatController {
    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    /**
     *  /pub/chat/message로 들어오는 메세징을 처리
     */
    @MessageMapping("/chat/message")
    public void message(ChatMessage message, @Header("token") String token) {
        String nickname = tokenProvider.getNickname(token);
        message.setSender(nickname);
        message.setUserCount(chatRoomRepository.getUserCount(message.getRoomId()));

        chatService.sendChatMessage(message);
    }
}
