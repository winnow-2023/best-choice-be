package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Notification;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    Slice<Notification> findByMember_IdOrderByCreatedDateDesc(long memberId, Pageable pageable);
}
