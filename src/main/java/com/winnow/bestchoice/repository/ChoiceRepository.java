package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Choice;
import com.winnow.bestchoice.entity.Member;
import com.winnow.bestchoice.entity.Post;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {

    boolean existsByPostAndMember(Post post, Member member);
}
