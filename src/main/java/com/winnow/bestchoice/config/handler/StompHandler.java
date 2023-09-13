package com.winnow.bestchoice.config.handler;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatMessage;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.repository.MemberRepository;
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

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

import static com.winnow.bestchoice.model.dto.ChatMessage.MessageType.*;

@Slf4j
@RequiredArgsConstructor
@Component
@Order(value = Ordered.HIGHEST_PRECEDENCE)
public class StompHandler implements ChannelInterceptor {

    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatService chatService;
    private final MemberRepository memberRepository;

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
        String sessionId = accessor.getSessionId();
        String jwtToken = getTokenByHeader(accessor);
        log.info("[CONNECT] 토큰 : {}, 세션 아이디 : {}", jwtToken, sessionId);
        tokenProvider.validToken(jwtToken);
    }

    private void enterProcess(Message<?> message, StompHeaderAccessor accessor) {
        String roomId = chatService.getRoomId(Objects.requireNonNull(accessor.getDestination()));
        String sessionId = accessor.getSessionId();

        if (!checkCapacity(roomId)) {
            throw new CustomException(ErrorCode.CHATROOM_CAPACITY_EXCEEDED);
        }

        chatRoomRepository.setUserEnterInfo(sessionId, roomId);
        chatRoomRepository.plusUserCount(roomId);

        String jwtToken = getTokenByHeader(accessor);
        Long memberId = tokenProvider.getMemberId(jwtToken);
        Member member = memberRepository.findById(memberId).get();

        String nickname = member.getNickname();
        sendChatMessage(ENTER, roomId, nickname);

        log.info("[SUBSCRIBED] 세션 아이디 : {}, 닉네임 : {}, 채팅방 : {}", sessionId, nickname, roomId);
    }

    private boolean checkCapacity(String roomId) {
        log.info("checkCapacity() 호출");
        long userCount = chatRoomRepository.getUserCount(roomId);
        log.info("현재 채팅방 유저수 : {}", userCount);

        return userCount < 10;
    }

    private void disconnectProcess(Message<?> message, StompHeaderAccessor accessor) {
        String sessionId = accessor.getSessionId();
        String roomId = chatRoomRepository.getUserEnterRoomId(sessionId);

        chatRoomRepository.minusUserCount(roomId);

        String jwtToken = getTokenByHeader(accessor);
        Long memberId = tokenProvider.getMemberId(jwtToken);
        Member member = memberRepository.findById(memberId).get();

        String nickname = member.getNickname();
        sendChatMessage(QUIT, roomId, nickname);
        chatRoomRepository.removeUserEnterInfo(sessionId);

        log.info("[DISCONNECTED] 세션 아이디: {}, 채팅방 : {}", sessionId, roomId);
    }

    private void sendChatMessage(ChatMessage.MessageType type, String roomId, String nickname) {
        chatService.sendChatMessage(ChatMessage.builder()
                .type(type)
                .roomId(roomId)
                .sender(nickname)
                .sendTime(LocalDateTime.now())
                .build());
    }

    private static String getTokenByHeader(StompHeaderAccessor accessor) {
        return accessor.getFirstNativeHeader("token");
    }

}
