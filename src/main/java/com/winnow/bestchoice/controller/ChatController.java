package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.model.dto.ChatMessage;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
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
    @MessageMapping("/chat/message/{roomId}")
    @SendTo("/sub/chat/room/{roomId}")
    public void message(ChatMessage message, @Header("token") String token, @DestinationVariable(value = "roomId") String roomId) {
        log.info("[ChatController.message 호출] roomId : {}, message : {}", roomId, message);

        String nickname = tokenProvider.getNickname(token);
        message.setSender(nickname);
        message.setUserCount(chatRoomRepository.getUserCount(roomId));

        chatService.sendChatMessage(message);
    }
}
