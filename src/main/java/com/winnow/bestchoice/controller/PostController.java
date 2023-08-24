package com.winnow.bestchoice.controller;

import com.winnow.bestchoice.model.request.CreatePostForm;
import com.winnow.bestchoice.model.response.PostDetailRes;
import com.winnow.bestchoice.service.PostService;
import com.winnow.bestchoice.type.Option;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/posts")
@Slf4j
public class PostController {

    private final PostService postService;

    @PostMapping
    public ResponseEntity<?> createPost(Authentication authentication,
                                        @Valid CreatePostForm createPostForm,
                                        List<MultipartFile> files) {

        PostDetailRes postDetail = postService.createPost(createPostForm, files, authentication);

        return ResponseEntity.status(HttpStatus.CREATED).body(postDetail);
    }

    @PostMapping("/{postId}/like")
    public ResponseEntity<?> likePost(Authentication authentication, @PathVariable long postId) {

        postService.likePost(authentication, postId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/unlike")
    public ResponseEntity<?> unlikePost(Authentication authentication, @PathVariable long postId) {

        postService.unlikePost(authentication, postId);

        return ResponseEntity.ok().build();
    }

    @PostMapping("/{postId}/choice")
    public ResponseEntity<?> choiceOption(Authentication authentication, @PathVariable long postId,
                                          @RequestParam Option option) {

        postService.choiceOption(authentication, postId, option);

        return ResponseEntity.ok().build();
    }
}
