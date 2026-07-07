package com.thiago.leiloa_api.service;

import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.item.ItemStatus;
import com.thiago.leiloa_api.repository.AuctionRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuctionLifecycleService {

    private final AuctionRepository auctionRepository;

    @Transactional
    public void suspendActiveAuctionsForUser(UUID userId) {
        List<Auction> auctions = auctionRepository.findByUser_IdAndStatusIn(
                userId,
                List.of(AuctionStatus.ACTIVE, AuctionStatus.SCHEDULED)
        );

        for (Auction auction : auctions) {
            auction.suspend();
            releaseItemForNewAuction(auction.getItem());
        }
    }

    private void releaseItemForNewAuction(Item item) {
        if (item.getStatus() != ItemStatus.SOLD && item.getStatus() != ItemStatus.DELETED) {
            item.setStatus(ItemStatus.ACTIVE);
        }
    }
}
