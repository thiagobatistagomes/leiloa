package com.thiago.leiloa_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thiago.leiloa_api.domain.notification.Notification;

public interface NotificationRepository extends JpaRepository<Notification, UUID> {

    List<Notification> findByUserIdAndReadAtIsNull(UUID userId);

    Page<Notification> findByUserId(UUID userId, Pageable pageable);

    List<Notification> findByUserIdAndCreatedAtAfter(UUID userId, LocalDateTime after);
}