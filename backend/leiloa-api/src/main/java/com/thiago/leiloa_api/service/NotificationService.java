package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.notification.Notification;

import com.thiago.leiloa_api.dto.notification.NotificationCreateDTO;
import com.thiago.leiloa_api.dto.notification.NotificationFilterDTO;
import com.thiago.leiloa_api.dto.notification.NotificationResponseDTO;
import com.thiago.leiloa_api.repository.NotificationRepository;
import com.thiago.leiloa_api.repository.NotificationTypeRepository;
import com.thiago.leiloa_api.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final NotificationTypeRepository typeRepository;
    private final UserRepository userRepository;

    // Pooling: Buscar notificações pendentes para um usuário
    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> poll(UUID userId, LocalDateTime after) {

        List<Notification> list =
                notificationRepository.findByUserIdAndCreatedAtAfter(userId, after);

        return list.stream()
                .map(this::toDTO)
                .toList();
    }

    // 1. Criar notificação
    @Transactional
    public void createNotification(NotificationCreateDTO dto) {

        var user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new EntityNotFoundException("Usuário não encontrado"));

        var type = typeRepository.findByCode(dto.typeCode())
                .orElseThrow(() -> new EntityNotFoundException("Tipo de notificação não encontrado"));

        Notification n = new Notification();
        n.setUser(user);
        n.setType(type);
        n.setTitle(dto.title());
        n.setMessage(dto.message());
        n.setData(dto.data());
        n.setCreatedAt(LocalDateTime.now());
        n.setReadAt(null);

        notificationRepository.save(n);
    }

    // 2. Marcar como lida
    @Transactional
    public void markAsRead(UUID id, UUID userId) {

        Notification n = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Notificação não encontrada"));

        if (!n.getUser().getId().equals(userId)) {
            throw new SecurityException("Notificação não pertence ao usuário");
        }

        if (n.getReadAt() == null) {
            n.setReadAt(LocalDateTime.now());
            notificationRepository.save(n);
        }
    }

    // 3. Marcar todas como lidas
    @Transactional
    public void markAllAsRead(UUID userId) {
        var list = notificationRepository.findByUserIdAndReadAtIsNull(userId);
        for (var n : list) {
            n.setReadAt(LocalDateTime.now());
        }
        notificationRepository.saveAll(list);
    }

    // 4. Buscar pendentes
  

    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> listUnreads(UUID userId, Pageable pageable) {

        Page<Notification> page = notificationRepository.findByUserId(userId, pageable);

        List<Notification> filtered = page.getContent().stream()
                .filter(n -> n.getReadAt() == null)
                .toList();

        return new PageImpl<>(
                filtered.stream().map(this::toDTO).toList(),
                pageable,
                page.getTotalElements()
        );
    }

    // 5. Listagem geral com filtros (para página "Ver todas")
    @Transactional(readOnly = true)
    public Page<NotificationResponseDTO> listAll(NotificationFilterDTO filter, Pageable pageable) {

        Page<Notification> page = notificationRepository.findByUserId(filter.userId(), pageable);

        List<Notification> filtered = page.getContent().stream()
                .filter(n -> {
                    if (filter.unread() != null) {
                        if (filter.unread() && n.getReadAt() != null) return false;
                        if (!filter.unread() && n.getReadAt() == null) return false;
                    }
                    if (filter.from() != null && n.getCreatedAt().isBefore(filter.from())) return false;
                    if (filter.to() != null && n.getCreatedAt().isAfter(filter.to())) return false;
                    return true;
                })
                .toList();

        return new PageImpl<>(
                filtered.stream().map(this::toDTO).toList(),
                pageable,
                page.getTotalElements()
        );
    }

    private NotificationResponseDTO toDTO(Notification n) {
        return new NotificationResponseDTO(
                n.getId(),
                n.getType().getCode(),
                n.getTitle(),
                n.getMessage(),
                n.getData(),
                n.getCreatedAt(),
                n.getReadAt()
        );
    }


}