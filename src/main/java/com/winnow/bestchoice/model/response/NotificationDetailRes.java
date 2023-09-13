package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Notification;
import com.winnow.bestchoice.entity.Post;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter @Setter
public class NotificationDetailRes {
    private long postId;
    private String postTitle;
    private String optionA;
    private String optionB;
    private boolean liveChatActive;
    private long liveChatUserCount;

    public static NotificationDetailRes of(Post post, long liveChatUserCount) {
        return NotificationDetailRes.builder()
                .postId(post.getId())
                .postTitle(post.getTitle())
                .optionA(post.getOptionA())
                .optionB(post.getOptionB())
                .liveChatActive(post.isLiveChatActive())
                .liveChatUserCount(liveChatUserCount)
                .build();
    }
}
