package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @Modifying
    @Query("update Comment c set c.likeCount=c.likeCount+1 where c.id=:id")
    void plusLikeCountById(long id);

    @Modifying
    @Query("update Comment c set c.likeCount=c.likeCount-1 where c.id=:id")
    void minusLikeCountById(long id);
}
