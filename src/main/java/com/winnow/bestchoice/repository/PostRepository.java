package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "member")
    Optional<Post> findWithMemberById(long postId);

    @EntityGraph(attributePaths = "member")
    Slice<Post> findSliceBy(Pageable pageable);

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

    @EntityGraph(attributePaths = "member")
    Slice<Post> findSliceByMember(Member member, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("select pl.post from PostLike pl where pl.member=:member")
    Slice<Post> findSliceFromPostLike(Member member, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("select c.post from Comment c where c.member=:member")
    Slice<Post> findSliceFromComment(Member member, Pageable pageable);

    @EntityGraph(attributePaths = "member")
    @Query("select c.post from Choice c where c.member=:member")
    Slice<Post> findSliceFromChoice(Member member, Pageable pageable);
}
