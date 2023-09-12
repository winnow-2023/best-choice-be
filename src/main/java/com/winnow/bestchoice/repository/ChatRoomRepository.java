package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.dto.ChatRoom;
import com.winnow.bestchoice.model.dto.ChatRoomPage;
import com.winnow.bestchoice.model.response.ChatRoomResponse;
import com.winnow.bestchoice.service.RedisSubscriber;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.listener.ChannelTopic;
import org.springframework.data.redis.listener.RedisMessageListenerContainer;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class ChatRoomRepository {
    // Redis CacheKeys
    private static final String CHAT_ROOMS = "CHAT_ROOM"; // 채팅룸 저장
    public static final String USER_COUNT = "USER_COUNT"; // 채팅룸에 입장한 클라이언트수 저장

    private final RedisMessageListenerContainer redisMessageListener;
    private final RedisSubscriber redisSubscriber;
    private final PostRepository postRepository;
    private final RedisTemplate<String, Object> redisTemplate;
    private HashOperations<String, String, ChatRoom> opsHashChatRoom;
    private Map<String, ChannelTopic> topics;

    @PostConstruct
    private void init() {
        opsHashChatRoom = redisTemplate.opsForHash();
        topics = new HashMap<>();
    }


    // 특정 채팅방 조회
    public ChatRoom findChatRoomByRoomId(String roomId) {
        return (ChatRoom) Optional.ofNullable(redisTemplate.opsForHash().get(CHAT_ROOMS, roomId)).orElseThrow(
                () -> new CustomException(ErrorCode.CHATROOM_NOT_FOUND)
        );
    }

    // 채팅방 목록 조회
    public ChatRoomPage<List<ChatRoomResponse>> findAllChatRoom(int pageNumber, int pageSize) {
        ArrayList<ChatRoomResponse> chatRooms = new ArrayList<>();
        Set<Object> roomIds = redisTemplate.opsForHash().keys(CHAT_ROOMS);

        for (Object roomId : roomIds) {
            Post post = postRepository.findById(Long.parseLong((String) roomId)).orElseThrow(
                    () -> new CustomException(ErrorCode.POST_NOT_FOUND));
            ChatRoom chatRoom = (ChatRoom) redisTemplate.opsForHash().get(CHAT_ROOMS, roomId);

            ChatRoomResponse chatRoomResponse = ChatRoomResponse.fromEntity(post, Objects.requireNonNull(chatRoom));

            chatRooms.add(chatRoomResponse);
        }

        chatRooms.sort((o1, o2) -> o2.getCreatedDate().compareTo(o1.getCreatedDate()));
        ChatRoomPage<?> chatRoomPage = new ChatRoomPage<>(chatRooms, pageSize);

        return (ChatRoomPage<List<ChatRoomResponse>>) chatRoomPage.getPage(pageNumber);
    }

    // 채팅방 생성
    public ChatRoom createChatRoom(String roomId) {
        ChatRoom chatRoom = ChatRoom.create(roomId);
        redisTemplate.opsForHash().put(CHAT_ROOMS, roomId, chatRoom);
        return chatRoom;
    }

    // 채팅방 입장
    public void enterChatRoom(String roomId) {
        ChannelTopic topic = topics.get(roomId);
        if (topic == null)
            topic = new ChannelTopic(roomId);
        redisMessageListener.addMessageListener((MessageListener) redisSubscriber, topic);
        topics.put(roomId, topic);
    }


    public ChannelTopic getTopic(String roomId) {
        return topics.get(roomId);
    }


    // 채팅방 유저수 조회
    public long getUserCount(String roomId) {
        return Long.parseLong((String) Optional.ofNullable(redisTemplate.opsForValue().get(USER_COUNT + "_" + roomId)).orElse("0"));
    }

    // 채팅방에 입장한 유저수 +1
    public long plusUserCount(String roomId) {
        return Optional.ofNullable(redisTemplate.opsForValue().increment(USER_COUNT + "_" + roomId)).orElse(0L);
    }

    // 채팅방에 입장한 유저수 -1
    public long minusUserCount(String roomId) {
        return Optional.ofNullable(redisTemplate.opsForValue().decrement(USER_COUNT + "_" + roomId))
                .filter(count -> count > 0).orElse(0L);
    }

    // 채팅방 삭제
    public void deleteChatRoom(String roomId) {
        redisTemplate.opsForHash().delete(CHAT_ROOMS, roomId);
        redisTemplate.opsForValue().getAndDelete(USER_COUNT + "_" + roomId);
    }

}
