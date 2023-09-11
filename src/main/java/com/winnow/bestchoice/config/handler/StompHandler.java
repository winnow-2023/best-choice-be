package com.winnow.bestchoice.config.handler;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatMessage;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.service.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.winnow.bestchoice.model.dto.ChatMessage.MessageType.*;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;

    // WebSocket을 통해 들어온 요청이 처리 되기 전 실행된다.
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        checkAccessor(message);
        return message;
    }

    private void checkAccessor(Message<?> message) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        if (StompCommand.CONNECT == accessor.getCommand()) {
            checkToken(accessor);
        } else if (StompCommand.SUBSCRIBE == accessor.getCommand()) {
            enterProcess(message, accessor);
        } else if (StompCommand.DISCONNECT == accessor.getCommand()) {
            disconnectProcess(message, accessor);
        }
    }

    private void checkToken(StompHeaderAccessor accessor) {
        String jwtToken = getTokenByHeader(accessor);
        log.info("[CONNECT] token : {}", jwtToken);
        tokenProvider.validToken(jwtToken);
    }

    private void enterProcess(Message<?> message, StompHeaderAccessor accessor) {
        String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders()
                .get("destination")).orElse("InvalidRoomId"));
//        String sessionId = (String) message.getHeaders().get("SessionId");

        if (!checkCapacity(roomId)) {
            throw new CustomException(ErrorCode.CHATROOM_CAPACITY_EXCEEDED);
        }

//        chatRoomRepository.setUserEnterInfo(sessionId, roomId);
        chatRoomRepository.plusUserCount(roomId);

        String nickname = getNicknameByToken(accessor);
        sendChatMessage(ENTER, roomId, nickname);

        log.info("[SUBSCRIBED] 닉네임 : {}, 채팅방 : {}", nickname, roomId);
    }

    private boolean checkCapacity(String roomId) {
        long userCount = chatRoomRepository.getUserCount(roomId);
        return userCount < 10;
    }

    private void disconnectProcess(Message<?> message, StompHeaderAccessor accessor) {
//        String sessionId = (String) message.getHeaders().get("simpSessionId");
        String roomId = chatService.getRoomId(Optional.ofNullable((String) message.getHeaders()
                .get("Destination")).orElse("InvalidRoomId"));

        chatRoomRepository.minusUserCount(roomId);

        String nickname = getNicknameByToken(accessor);
        sendChatMessage(QUIT, roomId, nickname);
//        chatRoomRepository.removeUserEnterInfo(sessionId);

        log.info("[DISCONNECTED] roomId : {}", roomId);
    }


    private void sendChatMessage(ChatMessage.MessageType type, String roomId, String nickname) {
        chatService.sendChatMessage(ChatMessage.builder()
                .type(type)
                .roomId(roomId)
                .sender(nickname)
                .build());
    }

    private String getNicknameByToken(StompHeaderAccessor accessor) {
        String token = getTokenByHeader(accessor);
        return tokenProvider.getNickname(token);
    }

    private static String getTokenByHeader(StompHeaderAccessor accessor) {
        return (String) accessor.getHeader("token");
    }

}
