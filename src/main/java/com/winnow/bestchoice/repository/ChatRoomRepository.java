package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatRoom;
import com.winnow.bestchoice.model.response.ChatRoomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장
    public static final String ENTER_INFO = "ENTER_INFO"; // 채팅룸에 입장한 클라이언트의 sessionId와 채팅룸 id를 맵핑한 정보 저장

    private static final long EXTEND_MINUTE = 30; // 30분

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

    public List<ChatRoomResponse> findAllChatRoom() {
        Set<String> roomIds = hashOpsChatRoom.keys(CHAT_ROOMS);
        List<ChatRoomResponse> chatRooms = roomIds.stream()
                .map(roomId -> postRepository.findById(Long.parseLong(roomId)))
                .filter(Optional::isPresent)
                .map(Optional::get)
                .map(chatRoom -> ChatRoomResponse.builder()
                        .title(chatRoom.getTitle())
                        .optionA(chatRoom.getOptionA())
                        .optionB(chatRoom.getOptionB())
                        .likeCount(chatRoom.getLikeCount())
                        .commentCount(chatRoom.getCommentCount())
                        .nickname(chatRoom.getMember().getNickname())
                        .createdDate(chatRoom.getCreatedDate())
                        .build()
                )
                .collect(Collectors.toList());

        return chatRooms;

    }

    // 채팅방 생성
    public ChatRoom createChatRoom(String roomId) {
        ChatRoom chatRoom = ChatRoom.create(roomId);
        hashOpsChatRoom.put(CHAT_ROOMS, roomId, chatRoom);
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

    // 채팅방 만료시간 연장(30분)
    public void extendExpireTime(String roomId) {
        ChatRoom chatRoom = Optional.ofNullable(hashOpsChatRoom.get(CHAT_ROOMS, roomId)).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );

        chatRoom.setExpireTime(chatRoom.getExpireTime().plusMinutes(EXTEND_MINUTE));
    }

}
