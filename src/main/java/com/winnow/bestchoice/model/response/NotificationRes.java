package com.winnow.bestchoice.model.response;

import com.winnow.bestchoice.entity.Notification;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@Builder
public class NotificationRes {

    private long id;
    private long postId;
    private boolean checked;
    private LocalDateTime createDate;

    public static NotificationRes of(Notification notification) {
        return NotificationRes.builder()
                .id(notification.getId())
                .postId(notification.getPost().getId())
                .checked(notification.isChecked())
                .createDate(notification.getCreatedDate())
                .build();
    }
}
