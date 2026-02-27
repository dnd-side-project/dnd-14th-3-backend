package com.dnd.jjigeojulge.notification.domain.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.dnd.jjigeojulge.notification.domain.Notification;

public interface NotificationRepository extends JpaRepository<Notification, Long>, NotificationQueryRepository {
}
