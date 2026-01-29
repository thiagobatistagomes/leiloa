package com.thiago.leiloa_api.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.thiago.leiloa_api.domain.bid.Bid;

public interface BidRepository extends JpaRepository<Bid, UUID> {

    List<Bid> findByAuctionIdOrderByCreatedAtDesc(UUID auctionId);

    boolean existsByAuctionId(UUID auctionId);

    Page<Bid> findByAuctionId(UUID auctionId, Pageable pageable);

    Page<Bid> findByBidderId(UUID bidderId, Pageable pageable);
}

