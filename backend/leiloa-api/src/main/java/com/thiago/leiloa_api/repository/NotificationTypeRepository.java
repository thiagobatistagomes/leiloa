package com.thiago.leiloa_api.repository;

import java.util.UUID;
import java.util.Optional;
import com.thiago.leiloa_api.domain.notification.NotificationType;
import org.springframework.data.jpa.repository.JpaRepository;



public interface NotificationTypeRepository extends JpaRepository<NotificationType, UUID> {
    boolean existsByCode(String code);
    Optional<NotificationType> findByCode(String code);
}
