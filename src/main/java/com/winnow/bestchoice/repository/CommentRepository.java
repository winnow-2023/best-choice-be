package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {
}
