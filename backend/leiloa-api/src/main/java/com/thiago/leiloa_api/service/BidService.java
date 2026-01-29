package com.thiago.leiloa_api.service;

import java.math.BigDecimal;
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

import jakarta.persistence.EntityNotFoundException;
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

        Auction auction = auctionRepository.findByIdForUpdate(dto.auctionId())
            .orElseThrow(() -> new EntityNotFoundException("Leilão não encontrado"));

        if (auction.getStatus() != AuctionStatus.ACTIVE) {
            throw new BusinessException("Leilão não está ativo.");
        }

        if (auction.isOwnedBy(bidder)) {
            throw new BusinessException("Você não pode dar lance no próprio leilão.");
        }

        BigDecimal bidValue = dto.value();
        BigDecimal currentPrice = auction.getCurrentPrice();
        BigDecimal minIncrement = auction.getMinIncrement();

        if (bidValue.compareTo(currentPrice) <= 0) {
            throw new BusinessException("O valor do lance deve ser maior que o preço atual.");
        }

        if (bidValue.subtract(currentPrice).compareTo(minIncrement) < 0) {
            throw new BusinessException(
                "O lance deve respeitar o incremento mínimo de " + minIncrement
            );
        }

        Bid bid = Bid.create(auction, bidder, bidValue);

        bidRepository.saveAndFlush(bid);

        auction.updateAfterBid(bidValue, bidder);

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

    public class BusinessException extends RuntimeException {
        public BusinessException(String message) {
            super(message);
        }
    }


}

