package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Attachment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface AttachmentRepository extends JpaRepository<Attachment, Long> {

    @Query("select a.url from Attachment a where a.post.id=:postId")
    List<String> findUrlsByPostId(long postId);
}
