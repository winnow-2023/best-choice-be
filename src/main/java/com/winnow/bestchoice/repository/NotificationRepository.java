package com.winnow.bestchoice.repository;

import com.winnow.bestchoice.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
}
