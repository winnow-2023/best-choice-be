package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.model.dto.ChatRoom;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Builder
@Getter
public class ChatRoomResponse{
    private String title;
    private String optionA;
    private String optionB;
    private long likeCount;
    private long commentCount;
    private String nickname;
    private LocalDateTime createdDate;
    private LocalDateTime chatRoomCreatedDate;
    private long userCount;

    public static ChatRoomResponse fromEntity(Post post, ChatRoom chatRoom) {
        return ChatRoomResponse.builder()
                .title(post.getTitle())
                .optionA(post.getOptionA())
                .optionB(post.getOptionB())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .nickname(post.getMember().getNickname())
                .createdDate(post.getCreatedDate())
                .userCount(chatRoom.getUserCount())
                .build();
    }

}
