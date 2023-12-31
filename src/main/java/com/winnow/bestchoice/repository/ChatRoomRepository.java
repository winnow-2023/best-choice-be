package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatRoom;
import com.winnow.bestchoice.model.dto.ChatRoomPage;
import com.winnow.bestchoice.model.response.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장


    private final PostRepository postRepository;

    @Resource(name = "redisTemplate")
    private HashOperations<String, String, ChatRoom> hashOpsChatRoom;
    @Resource(name = "redisTemplate")
    private HashOperations<String, String, String> hashOpsEnterInfo;
    @Resource(name = "redisTemplate")
    private ValueOperations<String, String> valueOps;

    // 특정 채팅방 조회
    public ChatRoom findChatRoomByRoomId(String roomId) {
        return Optional.ofNullable(hashOpsChatRoom.get(CHAT_ROOMS, roomId)).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );
    }

    // 채팅방 목록 조회
    public List<ChatRoomResponse> findAllChatRoom(int page, int size) {
        log.info("findAllChatRoom() 호출");
        List<ChatRoomResponse> chatRooms = new ArrayList<>();
        Set<String> roomIds = hashOpsChatRoom.keys(CHAT_ROOMS);
        log.info("{}", roomIds);

        for (String roomId : roomIds) {
            Post post = getPostByRoomId(roomId);

            ChatRoom chatRoom = hashOpsChatRoom.get(CHAT_ROOMS, roomId);
            Objects.requireNonNull(chatRoom).setUserCount(getUserCount(roomId));
            log.info("가져온 채팅방 : {}", chatRoom);

            CheckingUserCount(chatRoom, post, chatRooms);
        }
        if (!chatRooms.isEmpty()) {
            return getChatRoomResponses(page, size, chatRooms);
        }

        return chatRooms;

    }

    private static List<ChatRoomResponse> getChatRoomResponses(int page, int size, List<ChatRoomResponse> chatRooms) {
        chatRooms.sort((o1, o2) -> o2.getChatRoomCreatedDate().compareTo(o1.getChatRoomCreatedDate()));
        ChatRoomPage<ChatRoomResponse> pages = new ChatRoomPage<>(chatRooms, page, size);
        log.info("페이지 정보 : {}", pages);
        return pages.getPagedData();
    }

    private Post getPostByRoomId(String roomId) {
        return postRepository.findById(Long.parseLong(roomId)).orElseThrow(
                () -> new CustomException(ErrorCode.POST_NOT_FOUND));
    }


    private static void CheckingUserCount(ChatRoom chatRoom, Post post, List<ChatRoomResponse> chatRooms) {
        if (chatRoom.getUserCount() > 0) {
            ChatRoomResponse chatRoomResponse = ChatRoomResponse.fromEntity(post, Objects.requireNonNull(chatRoom));
            chatRooms.add(chatRoomResponse);
        }
    }

    // 채팅방 생성
    @Transactional
    public ChatRoom createChatRoom(long postId) {
        String roomId = String.valueOf(postId);
        ChatRoom chatRoom = ChatRoom.create(roomId);
        hashOpsChatRoom.put(CHAT_ROOMS, roomId, chatRoom);
        postRepository.activateLiveChatById(postId);
        return chatRoom;
    }


    // 유저가 입장한 채팅방ID와 유저 세션ID 맵핑 정보 저장
    public void setUserEnterInfo(String sessionId, String roomId) {
        hashOpsEnterInfo.put(ENTER_INFO, sessionId, roomId);
    }

    // 유저 세션으로 입장해 있는 채팅방 ID 조회
    public String getUserEnterRoomId(String sessionId) {
        return hashOpsEnterInfo.get(ENTER_INFO, sessionId);
    }

    // 유저 세션정보와 맵핑된 채팅방ID 삭제
    public void removeUserEnterInfo(String sessionId) {
        hashOpsEnterInfo.delete(ENTER_INFO, sessionId);
    }

    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.parseLong(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(valueOps.decrement(USER_COUNT + "_" + roomId))
                .filter(count -> count > 0).orElse(0L);
    }

    // 채팅방 삭제
    @Transactional
    public void deleteChatRoom(String roomId) {
        hashOpsChatRoom.delete(CHAT_ROOMS, roomId);
        postRepository.deactivateLiveChatById(Long.parseLong(roomId));
        long count = Long.parseLong(Optional.ofNullable(valueOps.get(USER_COUNT + "_" + roomId)).orElse("0"));
        valueOps.decrement(USER_COUNT + "_" + roomId, count - 1);
    }
}
