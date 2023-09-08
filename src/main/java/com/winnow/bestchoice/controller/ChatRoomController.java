package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatRoom;
import com.winnow.bestchoice.model.response.ChatRoomResponse;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.repository.PostRepository;
import com.winnow.bestchoice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final TokenProvider tokenProvider;
    private final ChatRoomRepository chatRoomRepository;
    private final PostService postService;

    /**
     *  채팅방 생성
     */
    @PostMapping("/chat/open/{postId}")
    public ResponseEntity<ChatRoom> createChatRoom(@PathVariable Long postId, Authentication authentication) {
        Long memberId = tokenProvider.getMemberId(authentication);
        Post post = postService.findByPostId(postId);
        Long PostMemberId = post.getMember().getId();

        validateRequest(memberId, PostMemberId, post);
        ChatRoom chatRoom = chatRoomRepository.createChatRoom(String.valueOf(postId));

        return ResponseEntity.ok().body(chatRoom);
    }

    /**
     * 채팅방 입장
     */
    @GetMapping("/chat/enter/{roomId}")
    public ResponseEntity<?> enterChatRoom(@PathVariable String roomId) {
        ChatRoom chatRoom = chatRoomRepository.findChatRoomByRoomId(roomId);
        return ResponseEntity.ok().body(chatRoom.getRoomId());
    }

    /**
     * 채팅방 리스트
     */
    @GetMapping("/chat/rooms")
    public ResponseEntity<List<ChatRoomResponse>> findAllChatRoom() {
        return ResponseEntity.ok().body(chatRoomRepository.findAllChatRoom());
    }

    /**
     * 채팅방 만료시간 연장
     */
    @PostMapping("/chat/extend/{roomId}")
    public ResponseEntity<?> extendExpiredTime(@PathVariable String roomId) {
        chatRoomRepository.extendExpireTime(roomId);
        return ResponseEntity.ok().build();
    }

    private static void validateRequest(Long memberId, Long PostMemberId, Post post) {
        if (!Objects.equals(memberId, PostMemberId)) {
            throw new CustomException(ErrorCode.POST_MEMBER_ID_MISS_MATCH);
        }

        if (post.isDeleted()) {
            throw new CustomException(ErrorCode.ALREADY_DELETED_POST);
        }
    }

}
