package com.thiago.leiloa_api.specification;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.domain.Specification;

import com.thiago.leiloa_api.domain.auction.Auction;
import com.thiago.leiloa_api.domain.auction.AuctionStatus;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;

public class AuctionSpecification {

    public static Specification<Auction> withStatuses(
            List<AuctionStatus> statuses
    ) {
        return (root, query, cb) -> {
            if (statuses == null || statuses.isEmpty()) {
                return cb.conjunction();
            }
            return root.get("status").in(statuses);
        };
    }

    public static Specification<Auction> withCategory(
            UUID categoryId
    ) {
        return (root, query, cb) -> {
            if (categoryId == null) {
                return cb.conjunction();
            }

            Join<?, ?> item = root.join("item", JoinType.INNER);
            Join<?, ?> category = item.join("category", JoinType.INNER);

            return cb.equal(category.get("id"), categoryId);
        };
    }

    public static Specification<Auction> withSearch(
            String search
    ) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) {
                return cb.conjunction();
            }

            Join<?, ?> item = root.join("item", JoinType.INNER);

            return cb.like(
                cb.lower(item.get("name")),
                "%" + search.toLowerCase() + "%"
            );
        };
    }

    public static Specification<Auction> build(
            List<AuctionStatus> statuses,
            UUID categoryId,
            String search
    ) {
        return Specification
                .where(withStatuses(statuses))
                .and(withCategory(categoryId))
                .and(withSearch(search));
    }
}

