package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Post;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = "member")
    Optional<Post> findWithMemberById(long postId);

    @EntityGraph(attributePaths = "member")
    Slice<Post> findSliceBy(Pageable pageable);
}
