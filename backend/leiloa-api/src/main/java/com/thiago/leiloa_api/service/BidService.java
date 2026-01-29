package com.thiago.leiloa_api.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.domain.bid.Bid;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.bid.BidPublicResponseDTO;
import com.thiago.leiloa_api.dto.bid.BidResponseDTO;
import com.thiago.leiloa_api.dto.bid.CreateBidDTO;
import com.thiago.leiloa_api.repository.AuctionRepository;
import com.thiago.leiloa_api.repository.BidRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BidService {

    private final AuctionRepository auctionRepository;
    private final BidRepository bidRepository;
    private final AuthService authService;

    @Transactional
    public BidResponseDTO placeBid(CreateBidDTO dto) {

        User bidder = authService.getAuthenticatedUser();

        Auction auction = auctionRepository.findById(dto.auctionId())
            .orElseThrow(() -> new IllegalArgumentException("Leilão não encontrado"));

        // 1. Leilão ativo
        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new IllegalStateException("Leilão não está ativo.");
        }

        // 2. Dono não pode dar lance
        if (auction.isOwnedBy(bidder)) {
            throw new IllegalStateException("Você não pode dar lance no próprio leilão.");
        }

        BigDecimal bidValue = dto.value();
        BigDecimal currentPrice = auction.getCurrentPrice();
        BigDecimal minIncrement = auction.getMinIncrement();

        // 3. Valor maior que o atual
        if (bidValue.compareTo(currentPrice) <= 0) {
            throw new IllegalArgumentException("O valor do lance deve ser maior que o preço atual.");
        }

        // 4. Respeitar incremento mínimo
        if (bidValue.subtract(currentPrice).compareTo(minIncrement) < 0) {
            throw new IllegalArgumentException(
                "O lance deve respeitar o incremento mínimo de " + minIncrement
            );
        }

        // 5. Criar bid
        Bid bid = new Bid();
        bid.setAuction(auction);
        bid.setBidder(bidder);
        bid.setValue(bidValue);
        bid.setCreatedAt(LocalDateTime.now());

        bidRepository.save(bid);

        // 6. Atualizar preço atual do leilão
        auction.setCurrentPrice(bidValue);

        return BidResponseDTO.fromEntity(bid);
    }

    // Listar bids públicos de um leilão com paginação
    public Page<BidPublicResponseDTO> listByAuction(UUID auctionId, Pageable pageable) {
        return bidRepository
                .findByAuctionId(auctionId, pageable)
                .map(BidPublicResponseDTO::fromEntity);
    }

    // Histórico do Usuário
    public Page<BidResponseDTO> listMyBids(UUID userId, Pageable pageable) {
        return bidRepository
                .findByBidderId(userId, pageable)
                .map(BidResponseDTO::fromEntity);
    }

    // Auditoria ADMIN
    public Page<BidResponseDTO> listAll(Pageable pageable) {
        return bidRepository
                .findAll(pageable)
                .map(BidResponseDTO::fromEntity);
    }

}

