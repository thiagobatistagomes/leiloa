package com.thiago.leiloa_api.config;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.thiago.leiloa_api.domain.notification.NotificationType;
import com.thiago.leiloa_api.domain.notification.NotificationTypeCodes;
import com.thiago.leiloa_api.repository.NotificationTypeRepository;

@Configuration
public class NotificationTypesInitializer {

    @Bean
    CommandLineRunner initNotificationTypes(NotificationTypeRepository repository) {
        return args -> {

            List<String> types = List.of(
                NotificationTypeCodes.USER_REGISTERED,
                NotificationTypeCodes.AUCTION_PUBLISHED,
                NotificationTypeCodes.NEW_BID,
                NotificationTypeCodes.OUT_BID,
                NotificationTypeCodes.TIME_REMAINING,
                NotificationTypeCodes.AUCTION_FINISHED,
                NotificationTypeCodes.PAYMENT_CREATED,
                NotificationTypeCodes.PAYMENT_APPROVED,
                NotificationTypeCodes.PAYMENT_FAILED,
                NotificationTypeCodes.PAYMENT_CANCELLED,
                NotificationTypeCodes.PAYMENT_EXPIRED
            );

            for (String code : types) {
                if (!repository.existsByCode(code)) {
                    NotificationType type = new NotificationType();
                    type.setCode(code);
                    type.setDescription(code.replace("_", " ").toLowerCase());
                    type.setCreatedAt(LocalDateTime.now());
                    repository.save(type);
                }
            }
        };
    }
}