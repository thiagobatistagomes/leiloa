package com.thiago.leiloa_api.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;


public interface AuctionRepository extends JpaRepository<Auction, UUID>, JpaSpecificationExecutor<Auction> {

    boolean existsByItem_IdAndStatusIn(
        UUID itemId,
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



}
