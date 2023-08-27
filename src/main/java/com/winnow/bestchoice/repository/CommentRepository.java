package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Comment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CommentRepository extends JpaRepository<Comment, Long> {

    @EntityGraph(attributePaths = "member") //member 같이 조회 - 조회 컬럼 최적화하기
    Page<Comment> findByPost_Id(long post_id, Pageable pageable);
}
