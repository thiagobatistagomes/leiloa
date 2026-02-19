package com.thiago.leiloa_api.specification;

import java.time.LocalDateTime;
import org.springframework.data.jpa.domain.Specification;
import com.thiago.leiloa_api.domain.delivery.Delivery;
import com.thiago.leiloa_api.domain.delivery.DeliveryStatus;
import com.thiago.leiloa_api.dto.delivery.DeliveryFilterDTO;

public class DeliverySpecification {

    public static Specification<Delivery> filter(DeliveryFilterDTO f) {

        return Specification.where(status(f.status()))
                .and(deliveryMethod(f.deliveryMethod()))
                .and(createdAtStart(f.createdAtStart()))
                .and(createdAtEnd(f.createdAtEnd()))
                .and(updatedAtStart(f.updatedAtStart()))
                .and(updatedAtEnd(f.updatedAtEnd()));
    }

    private static Specification<Delivery> status(DeliveryStatus status) {
        return (root, query, cb) -> 
            status == null ? null : cb.equal(root.get("status"), status);
    }

    private static Specification<Delivery> deliveryMethod(String m) {
        return (root, query, cb) -> 
            (m == null || m.isBlank()) ? null : cb.equal(root.get("deliveryMethod"), m);
    }

    private static Specification<Delivery> createdAtStart(LocalDateTime v) {
        return (root, query, cb) ->
            v == null ? null : cb.greaterThanOrEqualTo(root.get("createdAt"), v);
    }

    private static Specification<Delivery> createdAtEnd(LocalDateTime v) {
        return (root, query, cb) ->
            v == null ? null : cb.lessThanOrEqualTo(root.get("createdAt"), v);
    }

    private static Specification<Delivery> updatedAtStart(LocalDateTime v) {
        return (root, query, cb) ->
            v == null ? null : cb.greaterThanOrEqualTo(root.get("updatedAt"), v);
    }

    private static Specification<Delivery> updatedAtEnd(LocalDateTime v) {
        return (root, query, cb) ->
            v == null ? null : cb.lessThanOrEqualTo(root.get("updatedAt"), v);
    }
}
