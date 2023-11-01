package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.annotation.LoginMemberId;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatRoom;
import com.winnow.bestchoice.model.response.ChatRoomResponse;
import com.winnow.bestchoice.repository.ChatRoomRepository;
import com.winnow.bestchoice.service.NotificationService;
import com.winnow.bestchoice.service.PostService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatRoomController {

    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;
    private final PostService postService;

    /**
     *  채팅방 생성
     */
    @PostMapping("/chat/open/{postId}")
    public ResponseEntity<ChatRoom> createChatRoom(@PathVariable long postId, @LoginMemberId long memberId) {
        Post post = postService.findByPostId(postId);
        Long PostMemberId = post.getMember().getId();

        validateRequest(memberId, PostMemberId, post);
        ChatRoom chatRoom = chatRoomRepository.createChatRoom(postId);

        notificationService.notifyCreatingRoomByPost(post); //채팅방 생성시 해당 게시글 관련 유저들에게 비동기 알림

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
    public ResponseEntity<List<ChatRoomResponse>> findAllChatRoom(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size)
    {
        return ResponseEntity.ok().body(chatRoomRepository.findAllChatRoom(page, size));
    }

    /**
     * 채팅방 삭제
     */
    @DeleteMapping("/chat/rooms/{roomId}")
    public ResponseEntity<?> deleteChatRoom(@PathVariable String roomId, @LoginMemberId long memberId) {
        Long writerId = postService.findByPostId(Long.parseLong(roomId)).getMember().getId();

        if (!Objects.equals(memberId, writerId)) {
            throw new CustomException(ErrorCode.POST_MEMBER_ID_MISS_MATCH);
        }
        chatRoomRepository.deleteChatRoom(roomId);

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
