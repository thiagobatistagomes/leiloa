package com.thiago.leiloa_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;

import jakarta.persistence.LockModeType;


public interface AuctionRepository extends JpaRepository<Auction, UUID>, JpaSpecificationExecutor<Auction> {

    boolean existsByItem_IdAndStatusIn(
        UUID itemId,
        List<AuctionStatus> statuses
    );

    List<Auction> findByUser_IdAndStatusIn(
        UUID userId,
        List<AuctionStatus> statuses
    );


    Page<Auction> findByStatusIn(
        List<AuctionStatus> statuses,
        Pageable pageable
    );

    List<Auction> findByStatusAndStartDateLessThanEqual(
        AuctionStatus status,
        LocalDateTime date
    );

    List<Auction> findByStatusAndEndDateLessThanEqual(
        AuctionStatus status,
        LocalDateTime date
    );

    List<Auction> findByStatusAndEndDateBetween(
        AuctionStatus status,
        LocalDateTime from,
        LocalDateTime to
    );

    // Lock pessimisita para prevenir condições de corrida ao atualizar o lance mais alto
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("""
        SELECT a FROM Auction a
        WHERE a.id = :auctionId
    """)
    Optional<Auction> findByIdForUpdate(UUID auctionId);

}
