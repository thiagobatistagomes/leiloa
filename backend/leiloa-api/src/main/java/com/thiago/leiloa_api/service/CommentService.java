package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.comment.Comment;
import com.thiago.leiloa_api.domain.comment.CommentStatus;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.comment.CommentResponseDTO;
import com.thiago.leiloa_api.dto.comment.CommentTreeDTO;
import com.thiago.leiloa_api.dto.comment.CreateCommentDTO;
import com.thiago.leiloa_api.repository.AuctionRepository;
import com.thiago.leiloa_api.repository.CommentRepository;
import com.thiago.leiloa_api.specification.CommentSpecification;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;







@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final AuthService authService;
    private final AuctionRepository auctionRepository;

    // Criar comentário
    @Transactional
    public CommentResponseDTO create(CreateCommentDTO dto) {

        User user = authService.getAuthenticatedUser();

        if (dto.content() == null || dto.content().isBlank()) {
            throw new IllegalArgumentException("O conteúdo do comentário não pode ser vazio.");
        }

        Auction auction = auctionRepository.findById(dto.auctionId())
                .orElseThrow(() -> new IllegalArgumentException("Leilão não encontrado."));

        if (!auction.isActive() && !auction.isScheduled()) {
            throw new IllegalStateException("Não é permitido comentar em leilões finalizados ou cancelados.");
        }

        Comment parent = null;

        if (dto.parentCommentId() != null) {
            parent = commentRepository.findById(dto.parentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("Comentário pai não encontrado."));

            if (!parent.getAuction().getId().equals(dto.auctionId())) {
                throw new IllegalStateException("O comentário pai pertence a outro leilão.");
            }

            if (parent.getStatus() == CommentStatus.DELETED) {
                throw new IllegalStateException("Não é permitido responder a um comentário deletado.");
            }

            if (parent.getStatus() == CommentStatus.BLOCKED) {
                throw new IllegalStateException("Não é permitido responder a um comentário bloqueado.");
            }
        }

        Comment comment = new Comment();
        comment.setUser(user);
        comment.setAuction(auction);
        comment.setParent(parent);
        comment.setContent(dto.content());
        comment.setStatus(CommentStatus.VISIBLE);
        comment.setCreatedAt(LocalDateTime.now());

        Comment saved = commentRepository.save(comment);

        return CommentResponseDTO.fromEntity(saved);
    }

    // Retornar comentários paginados de um usuário autenticado
    public Page<CommentResponseDTO> getMyComments(
        UUID optionalAuctionId,
        Pageable pageable
    ) {
        User user = authService.getAuthenticatedUser();

        List<CommentStatus> allowedStatuses = List.of(
                CommentStatus.VISIBLE,
                CommentStatus.HIDDEN
        );

        Page<Comment> page;

        if (optionalAuctionId != null) {
            page = commentRepository.findByUserAndAuctionIdAndStatusIn(
                    user,
                    optionalAuctionId,
                    allowedStatuses,
                    pageable
            );
        } else {
            page = commentRepository.findByUserAndStatusIn(
                    user,
                    allowedStatuses,
                    pageable
            );
        }

        return page.map(CommentResponseDTO::fromEntity);
    }


    // Lista paginada pública de comentários por leilão
    public Page<CommentResponseDTO> getPublicCommentsByAuction(
        UUID auctionId,
        Pageable pageable
    ) {
        Page<Comment> page = commentRepository.findByAuctionIdAndStatus(
            auctionId,
            CommentStatus.VISIBLE,
            pageable
        );

        return page.map(CommentResponseDTO::fromEntity);
    }

   

    public Page<CommentResponseDTO> adminSearch(
        UUID auctionId,
        UUID userId,
        List<CommentStatus> status,
        UUID parentId,
        String query,
        LocalDateTime startDate,
        LocalDateTime endDate,
        Pageable pageable
    ) {

        Specification<Comment> spec = Specification
                .where(CommentSpecification.auctionId(auctionId))
                .and(CommentSpecification.userId(userId))
                .and(CommentSpecification.statusIn(status))
                .and(CommentSpecification.parentId(parentId))
                .and(CommentSpecification.textContains(query))
                .and(CommentSpecification.createdBetween(startDate, endDate));

        Page<Comment> page = commentRepository.findAll(spec, pageable);

        return page.map(CommentResponseDTO::fromEntity);
    }


    public Page<CommentResponseDTO> getPublicReplies(
        UUID commentId,
        Pageable pageable
    ) {
        Page<Comment> page = commentRepository.findByParentIdAndStatus(
                commentId,
                CommentStatus.VISIBLE,
                pageable
        );

        return page.map(CommentResponseDTO::fromEntity);
    }



    // Lista hierárquica de comentários públicos
    public List<CommentTreeDTO> getCommentTree(UUID auctionId) {

        List<Comment> roots = commentRepository
                .findByAuctionIdAndParentIdIsNullAndStatusIn(
                        auctionId,
                        List.of(CommentStatus.VISIBLE)
                );

        return roots.stream()
                .map(this::buildTree)
                .collect(Collectors.toList());
    }

    private CommentTreeDTO buildTree(Comment comment) {
        CommentTreeDTO dto = new CommentTreeDTO(
                comment.getId(),
                comment.getUser().getId(),
                comment.getUser().getName(),
                comment.getContent(),
                comment.getCreatedAt(),
                new ArrayList<>()
        );

        List<Comment> children = commentRepository.findByParentIdAndStatusIn(
                comment.getId(),
                List.of(CommentStatus.VISIBLE)
        );

        dto.getReplies().addAll(
                children.stream().map(this::buildTree).toList()
        );

        return dto;
    }


    public Comment block(UUID commentId) {
        Comment comment = findByIdOrThrow(commentId);
        comment.block();
        return commentRepository.save(comment);
    }

    public Comment unblock(UUID commentId) {
        Comment comment = findByIdOrThrow(commentId);
        comment.unblock();
        return commentRepository.save(comment);
    }

    public Comment hide(UUID commentId) {
        Comment comment = findByIdOrThrow(commentId);
        User currentUser = authService.getAuthenticatedUser();

        if (!comment.isAuthor(currentUser)) {
            throw new AccessDeniedException("Você só pode ocultar seus próprios comentários.");
        }

        comment.hide();
        return commentRepository.save(comment);
    }

    public Comment unhide(UUID commentId) {
        Comment comment = findByIdOrThrow(commentId);
        User currentUser = authService.getAuthenticatedUser();

        if (!comment.isAuthor(currentUser)) {
            throw new AccessDeniedException("Você só pode reexibir seus próprios comentários.");
        }

        comment.unhide();
        return commentRepository.save(comment);
    }

    public Comment deleteUser(UUID commentId) {
        Comment comment = findByIdOrThrow(commentId);
        User currentUser = authService.getAuthenticatedUser();

        if (!comment.isAuthor(currentUser)) {
            throw new AccessDeniedException("Você só pode deletar seus próprios comentários.");
        }

        comment.deleteUser();
        return commentRepository.save(comment);
    }

    // Editar conteúdo de comentário (opcional)
    @Transactional
    public CommentResponseDTO updateContent(UUID commentId, String newContent) {
        Comment comment = findByIdOrThrow(commentId);
        User currentUser = authService.getAuthenticatedUser();

        if (!comment.isAuthor(currentUser)) {
            throw new AccessDeniedException("Você só pode editar seus próprios comentários.");
        }

        if (newContent == null || newContent.isBlank()) {
            throw new IllegalArgumentException("O conteúdo do comentário não pode ser vazio.");
        }

        comment.setContent(newContent);
        comment.setUpdatedAt(LocalDateTime.now());

        Comment updated = commentRepository.save(comment);

        return CommentResponseDTO.fromEntity(updated);
    }

    // ------------------------------------------
    // Métodos internos
    // ------------------------------------------

    private Comment findByIdOrThrow(UUID id) {
        return commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Comentário não encontrado"));
    }
}
