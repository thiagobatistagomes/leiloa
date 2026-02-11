package com.thiago.leiloa_api.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;
import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.user.User;
import com.thiago.leiloa_api.dto.auction.AuctionDetailResponseDTO;
import com.thiago.leiloa_api.dto.auction.AuctionFilterDTO;
import com.thiago.leiloa_api.dto.auction.AuctionListResponseDTO;
import com.thiago.leiloa_api.dto.auction.AuctionResponseDTO;
import com.thiago.leiloa_api.dto.auction.CreateAuctionDTO;
import com.thiago.leiloa_api.repository.AuctionRepository;
import com.thiago.leiloa_api.repository.ItemRepository;
import com.thiago.leiloa_api.specification.AuctionSpecification;

import lombok.RequiredArgsConstructor;




@Service
@RequiredArgsConstructor
public class AuctionService {

    private final AuctionRepository auctionRepository;
    private final ItemRepository itemRepository;
    private final AuthService authService;


    @Transactional
    public AuctionResponseDTO create(CreateAuctionDTO dto) {

        // 1. Usuário autenticado
        User user = authService.getAuthenticatedUser();

        // 2. Buscar item
        Item item = itemRepository.findById(dto.itemId())
                .orElseThrow(() -> new IllegalArgumentException("Item não encontrado"));

        // 3. Validar estado do item
        if (!item.canBeAuctioned()) {
            throw new IllegalStateException("Item não está ativo para leilão.");
        }

        // 4. Validar dono do item
        if (!item.isOwnedBy(user.getId())) {
            throw new IllegalStateException("Você não é o dono do item.");
        }

        // 5. Verificar leilões existentes
        boolean auctionExists = auctionRepository.existsByItem_IdAndStatusIn(
                item.getId(),
                List.of(AuctionStatus.ACTIVE, AuctionStatus.SCHEDULED)
        );

        if (auctionExists) {
            throw new IllegalStateException("O item já possui um leilão ativo ou agendado.");
        }

        // 6. Verificar se o item já possui um leilão finalizado
        if (auctionRepository.existsByItem_IdAndStatusIn(item.getId(), List.of(AuctionStatus.SOLD))) {
            throw new IllegalStateException("O item já foi leiloado anteriormente.");
        }

        // 7. Validar datas
        if (dto.startDate().isAfter(dto.endDate())) {
            throw new IllegalArgumentException("O início do leilão deve ser antes do fim.");
        }

        // 8. Definir status inicial
        AuctionStatus status = dto.startDate().isAfter(LocalDateTime.now())
                ? AuctionStatus.SCHEDULED
                : AuctionStatus.ACTIVE;

        // 9. Criar entidade
        Auction auction = new Auction();
        auction.setItem(item);
        auction.setUser(user);
        auction.setStartPrice(dto.startPrice());
        auction.setCurrentPrice(dto.startPrice());
        auction.setMinIncrement(dto.minIncrement());
        auction.setStartDate(dto.startDate());
        auction.setEndDate(dto.endDate());
        auction.setStatus(status);

        // 10. Persistir
        Auction saved = auctionRepository.save(auction);

        // 11. Retornar DTO
        return AuctionResponseDTO.fromEntity(saved);
    }


    @Transactional(readOnly = true)
    public Page<AuctionListResponseDTO> listPublicAuctions(
        AuctionFilterDTO filter,
        Pageable pageable
    ) {

        List<AuctionStatus> statuses = filter.statuses();
        if (statuses == null || statuses.isEmpty()) {
            statuses = List.of(
                AuctionStatus.ACTIVE,
                AuctionStatus.SCHEDULED
            );
        }

        var spec = AuctionSpecification.build(
            statuses,
            filter.categoryId(),
            filter.search()
        );

        return auctionRepository
            .findAll(spec, pageable)
            .map(AuctionListResponseDTO::fromEntity);

    }

    @Transactional(readOnly = true)
    public AuctionDetailResponseDTO getPublicAuction(UUID auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Leilão não encontrado"));

        // Regra de visibilidade
        if (auction.getStatus() == AuctionStatus.CANCELED) {
            throw new IllegalStateException("Leilão não disponível");
        }

        Item item = auction.getItem();
        User seller = item.getSeller();

        return new AuctionDetailResponseDTO(
                auction.getId(),

                auction.getStartPrice(),
                auction.getCurrentPrice(),
                auction.getMinIncrement(),

                auction.getStartDate(),
                auction.getEndDate(),
                auction.getStatus(),

                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getImageUrl(),

                seller.getId(),
                seller.getName()
        );
    }

    @Transactional
    public void finish(UUID auctionId) {

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Leilão não encontrado"));

        if (!auction.isActive()) {
            throw new IllegalStateException("Apenas leilões ativos podem ser finalizados.");
        }

        if (LocalDateTime.now().isBefore(auction.getEndDate())) {
            throw new IllegalStateException("O leilão ainda não chegou ao fim.");
        }

        auction.waitForPayment();
    }

    @Transactional
    public void cancel(UUID auctionId) {

        User user = authService.getAuthenticatedUser();

        Auction auction = auctionRepository.findById(auctionId)
                .orElseThrow(() -> new IllegalArgumentException("Leilão não encontrado"));

        if (!auction.isOwnedBy(user)) {
            throw new AccessDeniedException("Você não é o dono deste leilão.");
        }

        auction.cancel();
    }



    
}

