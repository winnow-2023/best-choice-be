package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.Comment;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.request.CreateCommentForm;
import com.winnow.bestchoice.repository.CommentRepository;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final TokenProvider tokenProvider;

    public void createComment(Authentication authentication, long postId, CreateCommentForm commentForm) {
        long memberId = tokenProvider.getMemberId(authentication);

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        Member member = memberRepository.getReferenceById(memberId);
        Post post = postRepository.getReferenceById(postId);

        commentRepository.save(Comment.builder()
                .member(member)
                .post(post)
                .content(commentForm.getContent()).build());
    }
}
