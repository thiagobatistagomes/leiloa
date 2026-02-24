package com.thiago.leiloa_api.domain.user;

import java.time.LocalDateTime;
import java.util.UUID;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "user_devices")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class UserDevice {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "device_token", nullable = false)
    private String deviceToken;

    @Column(name = "device_type", nullable = false)
    private String deviceType;

    @Column(name = "user_agent")
    private String userAgent;

    @Column(name = "last_used_at")
    private LocalDateTime lastUsedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
}
