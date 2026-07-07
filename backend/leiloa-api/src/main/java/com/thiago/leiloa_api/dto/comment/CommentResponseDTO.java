package com.thiago.leiloa_api.dto.comment;

import java.time.LocalDateTime;
import java.util.UUID;

import com.thiago.leiloa_api.domain.comment.Comment;
import com.thiago.leiloa_api.domain.comment.CommentStatus;

public record CommentResponseDTO(
    UUID id,
    UUID auctionId,
    UUID userId,
    String userName,
    UUID parentCommentId,
    String content,
    CommentStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {
    public static CommentResponseDTO fromEntity(Comment comment) {
        return new CommentResponseDTO(
            comment.getId(),
            comment.getAuction().getId(),
            comment.getUser().getId(),
            comment.getUser().getName(),
            comment.getParent() != null ? comment.getParent().getId() : null,
            comment.getContent(),
            comment.getStatus(),
            comment.getCreatedAt(),
            comment.getUpdatedAt()
        );
    }

}
