package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface PostRepository extends JpaRepository<Post, Long> {

    @Modifying
    @Query("update Post p set p.likeCount=p.likeCount+1 where p.id=:id")
    void plusLikeCountById(long id);

    @Modifying
    @Query("update Post p set p.likeCount=p.likeCount-1 where p.id=:id")
    void minusLikeCountById(long id);

    @Modifying
    @Query("update Post p set p.ACount=p.ACount+1 where p.id=:id")
    void plusACountById(long id);

    @Modifying
    @Query("update Post p set p.BCount=p.BCount+1 where p.id=:id")
    void plusBCountById(long id);

    @Modifying
    @Query("update Post p set p.commentCount=p.commentCount+1 where p.id=:id")
    void plusCommentCountById(long id);

    boolean existsByIdAndDeletedFalse(long postId);
}
