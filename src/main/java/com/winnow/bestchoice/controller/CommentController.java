package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/{commentId}/like")
    public ResponseEntity<?> likeComment(Authentication authentication, @PathVariable long commentId) {

        commentService.likeComment(authentication, commentId);

        return ResponseEntity.ok().build();
    }
}
