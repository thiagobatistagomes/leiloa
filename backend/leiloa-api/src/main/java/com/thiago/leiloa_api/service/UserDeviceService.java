package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.domain.user.UserDevice;
import com.thiago.leiloa_api.dto.device.UserDeviceCreateDTO;
import com.thiago.leiloa_api.repository.UserDeviceRepository;

import jakarta.persistence.EntityNotFoundException;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class UserDeviceService {

    private final UserDeviceRepository repository;

    @Transactional
    public UserDevice registerDevice(User user, UserDeviceCreateDTO dto) {

        UserDevice device = new UserDevice();
        device.setUser(user);
        device.setDeviceType(dto.deviceType());
        device.setUserAgent(dto.userAgent());
        device.setDeviceToken(dto.deviceToken());
        device.setCreatedAt(LocalDateTime.now());
        device.setLastUsedAt(LocalDateTime.now());

        return repository.save(device);
    }

    @Transactional(readOnly = true)
    public Page<UserDevice> findAll(Pageable pageable) {
        return repository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public List<UserDevice> findByUserId(UUID userId) {
        return repository.findByUserId(userId);
    }

    @Transactional(readOnly = true)
    public UserDevice findById(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Device não encontrado"));
    }

    @Transactional
    public void updateLastUsed(UUID deviceId) {
        repository.findById(deviceId).ifPresent(device -> {
            device.setLastUsedAt(LocalDateTime.now());
            repository.save(device);
        });
    }
}