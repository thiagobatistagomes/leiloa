package com.thiago.leiloa_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thiago.leiloa_api.domain.user.UserDevice;

public interface UserDeviceRepository extends JpaRepository<UserDevice, UUID> {

    List<UserDevice> findByUserId(UUID userId);

}
