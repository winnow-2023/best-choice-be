package com.winnow.bestchoice.service;

import com.winnow.bestchoice.config.jwt.TokenProvider;
import com.winnow.bestchoice.entity.Comment;
import com.winnow.bestchoice.entity.CommentLike;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import com.winnow.bestchoice.exception.CustomException;
import com.winnow.bestchoice.exception.ErrorCode;
import com.winnow.bestchoice.model.request.CreateCommentForm;
import com.winnow.bestchoice.model.response.CommentRes;
import com.winnow.bestchoice.repository.CommentLikeRepository;
import com.winnow.bestchoice.repository.CommentRepository;
import com.winnow.bestchoice.repository.MemberRepository;
import com.winnow.bestchoice.repository.PostRepository;
import com.winnow.bestchoice.type.CommentSort;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;
    private final PostRepository postRepository;
    private final CommentLikeRepository commentLikeRepository;
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

    public void likeComment(Authentication authentication, long commentId) {
        long memberId = tokenProvider.getMemberId(authentication);

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Member member = memberRepository.getReferenceById(memberId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (commentLikeRepository.existsByCommentAndMember(comment, member)) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        comment.setLikeCount(comment.getLikeCount() + 1);
        commentLikeRepository.save(new CommentLike(member, comment));
    }

    public void unlikeComment(Authentication authentication, long commentId) {
        long memberId = tokenProvider.getMemberId(authentication);

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Member member = memberRepository.getReferenceById(memberId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        CommentLike commentLike = commentLikeRepository.findByCommentAndMember(comment, member)
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_REQUEST));

        comment.setLikeCount(comment.getLikeCount() - 1);
        commentLikeRepository.delete(commentLike);
    }

    public void deleteComment(Authentication authentication, long commentId) { //comment에 삭제일만 넣어줌 - 추후 변경
        long memberId = tokenProvider.getMemberId(authentication);

        if (!memberRepository.existsById(memberId)) {
            throw new CustomException(ErrorCode.MEMBER_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new CustomException(ErrorCode.COMMENT_NOT_FOUND));

        if (comment.getMember().getId() != memberId || comment.getDeletedDate() != null) {
            throw new CustomException(ErrorCode.INVALID_REQUEST);
        }

        comment.setDeletedDate(LocalDateTime.now());
    }

    public Page<CommentRes> getComments(long postId, int page, int size, CommentSort sort) {
        if (!postRepository.existsById(postId)) {
            throw new CustomException(ErrorCode.POST_NOT_FOUND);
        }

        Post post = postRepository.getReferenceById(postId);

        PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sort.getValue()));
        return commentRepository.findByPost(post, pageRequest).map(CommentRes::of);
    }
}
