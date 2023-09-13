package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.annotation.LoginMemberId;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.service.NotificationService;
import com.winnow.bestchoice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PostService postService;

    @GetMapping(value = "/notifications/sub/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable long id) {
        return notificationService.subscribe(id);
    }

    @PostMapping("/send-data/{postId}")
    public void sendData(@PathVariable Long postId) {//todo test용 추후 삭제 필수
        Post post = postService.findByPostId(postId);
        notificationService.notifyCreatingRoomByPost(post);
    }

    @GetMapping("api/notifications")
    public ResponseEntity<?> getNotifications(@LoginMemberId long memberId,
                                              @RequestParam(defaultValue = "0") int page,
                                              @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(memberId, page, size));
    }

    @DeleteMapping("api/notifications/{notificationId}")
    public ResponseEntity<?> deleteNotification(@LoginMemberId long memberId, @PathVariable long notificationId) {
        notificationService.deleteNotification(memberId, notificationId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("api/notifications")
    public ResponseEntity<?> deleteAllNotifications(@LoginMemberId long memberId) {
        notificationService.deleteAllNotifications(memberId);
        return ResponseEntity.ok().build();
    }
}
