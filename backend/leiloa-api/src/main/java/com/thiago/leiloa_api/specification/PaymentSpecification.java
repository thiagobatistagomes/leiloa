package com.thiago.leiloa_api.specification;

import org.springframework.data.jpa.domain.Specification;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.dto.payment.PaymentFilterDTO;

public class PaymentSpecification {

    public static Specification<Payment> filter(PaymentFilterDTO f) {
        return Specification.where(hasAuctionId(f.getAuctionId()))
                .and(hasWinnerId(f.getWinnerId()))
                .and(hasStatus(f.getStatus()))
                .and(createdAtBetween(f.getCreatedAtStart(), f.getCreatedAtEnd()))
                .and(paidAtBetween(f.getPaidAtStart(), f.getPaidAtEnd()))
                .and(valueBetween(f.getMinValue(), f.getMaxValue()));
    }

    private static Specification<Payment> hasAuctionId(java.util.UUID id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("auction").get("id"), id);
    }

    private static Specification<Payment> hasWinnerId(java.util.UUID id) {
        return (root, query, cb) ->
                id == null ? null : cb.equal(root.get("winner").get("id"), id);
    }

    private static Specification<Payment> hasStatus(com.thiago.leiloa_api.domain.payment.PaymentStatus status) {
        return (root, query, cb) ->
                status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Payment> createdAtBetween(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    ) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("createdAt"), start, end);
            }
            if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("createdAt"), start);
            }
            if (end != null) {
                return cb.lessThanOrEqualTo(root.get("createdAt"), end);
            }
            return null;
        };
    }

    private static Specification<Payment> paidAtBetween(
            java.time.LocalDateTime start,
            java.time.LocalDateTime end
    ) {
        return (root, query, cb) -> {
            if (start != null && end != null) {
                return cb.between(root.get("paidAt"), start, end);
            }
            if (start != null) {
                return cb.greaterThanOrEqualTo(root.get("paidAt"), start);
            }
            if (end != null) {
                return cb.lessThanOrEqualTo(root.get("paidAt"), end);
            }
            return null;
        };
    }

    private static Specification<Payment> valueBetween(
            java.math.BigDecimal min,
            java.math.BigDecimal max
    ) {
        return (root, query, cb) -> {
            if (min != null && max != null) {
                return cb.between(root.get("value"), min, max);
            }
            if (min != null) {
                return cb.greaterThanOrEqualTo(root.get("value"), min);
            }
            if (max != null) {
                return cb.lessThanOrEqualTo(root.get("value"), max);
            }
            return null;
        };
    }
}

