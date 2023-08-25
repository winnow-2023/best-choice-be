package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Comment;
import com.winnow.bestchoice.entity.CommentLike;
import com.winnow.bestchoice.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CommentLikeRepository extends JpaRepository<CommentLike, Long> {

    boolean existsByCommentAndMember(Comment comment, Member member);

    Optional<CommentLike> findByCommentAndMember(Comment comment, Member member);
}
