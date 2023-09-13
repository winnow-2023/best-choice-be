package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class NotificationRes {

    private long notificationId;
    private long postId;
    private String postTitle;
    private boolean checked;
    private LocalDateTime createdDate;

    public static NotificationRes of(Notification notification) {
        return NotificationRes.builder()
                .notificationId(notification.getId())
                .postId(notification.getPost().getId())
                .postTitle(notification.getPostTitle())
                .checked(notification.isChecked())
                .createdDate(notification.getCreatedDate())
                .build();
    }
}
