package com.winnow.bestchoice.model.dto;

import com.winnow.bestchoice.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
@AllArgsConstructor
public class CreatingRoomNotificationData {

    private long postId;
    private String title;

    public static CreatingRoomNotificationData of(Post post) {
        return CreatingRoomNotificationData.builder()
                .postId(post.getId())
                .title(post.getTitle())
                .build();
    }
}
