package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.annotation.LoginMemberId;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.model.response.NotificationDetailRes;
import com.winnow.bestchoice.model.response.NotificationRes;
import com.winnow.bestchoice.service.NotificationService;
import com.winnow.bestchoice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Slice;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PostService postService;

    /**
     *  sse 연결 요청
     */
    @GetMapping(value = "/notifications/sub/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable long id) {
        return notificationService.subscribe(id);
    }

    @PostMapping("/send-data/{postId}")
    public void sendData(@PathVariable Long postId) {//todo test용 추후 삭제 필수
        Post post = postService.findByPostId(postId);
        notificationService.notifyCreatingRoomByPost(post);
    }

    /**
     *  알림 목록 조회
     */
    @GetMapping("api/notifications/all")
    public ResponseEntity<Slice<NotificationRes>> getNotifications(@LoginMemberId long memberId,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(notificationService.getNotifications(memberId, page, size));
    }

    /**
     *  알림 상세 조회
     */
    @GetMapping("api/notifications/{notificationId}")
    public ResponseEntity<NotificationDetailRes> getNotificationDetail(@LoginMemberId long memberId,
                                                                       @PathVariable long notificationId) {
        return ResponseEntity.ok(notificationService.getNotificationDetail(memberId, notificationId));
    }

    /**
     *  알림 삭제
     */
    @DeleteMapping("api/notifications/{notificationId}")
    public ResponseEntity<?> deleteNotification(@LoginMemberId long memberId, @PathVariable long notificationId) {
        notificationService.deleteNotification(memberId, notificationId);
        return ResponseEntity.ok().build();
    }

    /**
     *  알림 전체 삭제
     */
    @DeleteMapping("api/notifications")
    public ResponseEntity<?> deleteAllNotifications(@LoginMemberId long memberId) {
        notificationService.deleteAllNotifications(memberId);
        return ResponseEntity.ok().build();
    }
}
