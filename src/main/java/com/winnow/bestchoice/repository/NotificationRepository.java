package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findByMember_IdOrderByCreatedDateDesc(long memberId, Pageable pageable);

    @Modifying
    @Query("delete from Notification n where n.member.id=:memberId")
    void deleteAllByMemberId(long memberId);
}
