package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.PostLike;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {

    boolean existsByPost_IdAndMember_Id(long post_id, long member_id);

    Optional<PostLike> findByPost_IdAndMember_Id(long post_id, long member_id);
}
