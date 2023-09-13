package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

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

    @Modifying
    @Query("update Post p set p.commentCount=p.commentCount-1 where p.id=:id")
    void minusCommentCountById(long id);

    @Modifying
    @Query("update Post p set p.liveChatActive=true where p.id=:id")
    void activateLiveChatById(long id);

    @Modifying
    @Query("update Post p set p.liveChatActive=false where p.id=:id")
    void deactivateLiveChatById(long id);

    Optional<Post> findByIdAndDeletedFalse(long postId);

    boolean existsByIdAndDeletedFalse(long postId);
}
