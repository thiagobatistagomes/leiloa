package com.thiago.leiloa_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.thiago.leiloa_api.domain.comment.Comment;
import com.thiago.leiloa_api.domain.comment.CommentStatus;
import com.thiago.leiloa_api.domain.user.User;

public interface CommentRepository extends JpaRepository<Comment, UUID>, JpaSpecificationExecutor<Comment> {

    List<Comment> findByAuctionIdOrderByCreatedAtAsc(UUID auctionId);

    List<Comment> findByParentId(UUID parentId);

    @Query("""
       SELECT c FROM Comment c
       WHERE c.auction.id = :auctionId
         AND c.status IN :allowedStatuses
         AND (:fromDate IS NULL OR c.createdAt >= :fromDate)
         AND (:toDate IS NULL OR c.createdAt <= :toDate)
       """)
    Page<Comment> findByAuctionWithFilters(
            @Param("auctionId") UUID auctionId,
            @Param("allowedStatuses") List<CommentStatus> allowedStatuses,
            @Param("fromDate") LocalDateTime fromDate,
            @Param("toDate") LocalDateTime toDate,
            Pageable pageable
    );


    Page<Comment> findByUserAndStatusIn(
        User user,
        List<CommentStatus> statuses,
        Pageable pageable
    );

    Page<Comment> findByUserAndAuctionIdAndStatusIn(
        User user,
        UUID auctionId,
        List<CommentStatus> statuses,
        Pageable pageable
    );

    Page<Comment> findByAuctionIdAndStatus(
        UUID auctionId,
        CommentStatus status,
        Pageable pageable
    );

    Page<Comment> findByAuctionIdAndStatusIn(
        UUID auctionId,
        List<CommentStatus> statuses,
        Pageable pageable
    );

    Page<Comment> findByParentIdAndStatus(
        UUID parentId,
        CommentStatus status,
        Pageable pageable
    );

    Page<Comment> findByParentIdAndStatusIn(
        UUID parentId,
        List<CommentStatus> statuses,
        Pageable pageable
    );

    Page<Comment> findByParentIdAndUserAndStatusIn(
        UUID parentId,
        User user,
        List<CommentStatus> statuses,
        Pageable pageable
    );

    List<Comment> findByAuctionIdAndParentIdIsNullAndStatusIn(
        UUID auctionId,
        List<CommentStatus> statuses
    );

    List<Comment> findByParentIdAndStatusIn(
        UUID parentId,
        List<CommentStatus> statuses
    );

}
