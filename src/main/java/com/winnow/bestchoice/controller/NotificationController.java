package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.service.NotificationService;
import com.winnow.bestchoice.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@RestController
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;
    private final PostService postService;

    @GetMapping(value = "/notification/sub/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable long id) {
        return notificationService.subscribe(id);
    }

    @PostMapping("/send-data/{postId}")
    public void sendData(@PathVariable Long postId) {
        Post post = postService.findByPostId(postId);
        notificationService.notifyCreatingRoomByPostId(post);
    }
}
