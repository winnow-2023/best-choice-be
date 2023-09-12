package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.service.NotificationService;
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

    @GetMapping(value = "/notification/sub/{id}", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter subscribe(@PathVariable long id) {
        return notificationService.subscribe(id);
    }

    @PostMapping("/send-data/{id}")
    public void sendData(@PathVariable Long id) {
        notificationService.notify(id, "test입니다!");
    }
}
