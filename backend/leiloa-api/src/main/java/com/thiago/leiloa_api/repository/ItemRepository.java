package com.thiago.leiloa_api.repository;



import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.thiago.leiloa_api.domain.item.Item;
import com.thiago.leiloa_api.domain.user.User;

public interface ItemRepository extends JpaRepository<Item, UUID> {

    List<Item> findAllBySeller(User seller);

    Optional<Item> findByIdAndSeller(UUID id, User seller);
}
