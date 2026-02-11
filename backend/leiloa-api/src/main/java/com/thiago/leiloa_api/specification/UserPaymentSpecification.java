package com.thiago.leiloa_api.specification;

import java.util.ArrayList;
import java.util.UUID;
import java.util.List;
import jakarta.persistence.criteria.Predicate;


import org.springframework.data.jpa.domain.Specification;

import com.thiago.leiloa_api.domain.payment.Payment;
import com.thiago.leiloa_api.domain.payment.PaymentStatus;
import com.thiago.leiloa_api.dto.payment.MyPaymentFilterDTO;

public class UserPaymentSpecification {

    public static Specification<Payment> filter(UUID userId, MyPaymentFilterDTO filter) {
        return (root, query, cb) -> {

            List<Predicate> predicates = new ArrayList<>();

            // pagamento do usuário autenticado
            predicates.add(
                cb.equal(root.get("winner").get("id"), userId)
            );

            // EXCLUIR pending automaticamente
            predicates.add(
                cb.notEqual(root.get("status"), PaymentStatus.PENDING)
            );

            // status IN (...)
            if (filter.getStatuses() != null && !filter.getStatuses().isEmpty()) {
                predicates.add(
                    root.get("status").in(filter.getStatuses())
                );
            }

            if (filter.getMinValue() != null) {
                predicates.add(
                    cb.greaterThanOrEqualTo(root.get("value"), filter.getMinValue())
                );
            }

            if (filter.getMaxValue() != null) {
                predicates.add(
                    cb.lessThanOrEqualTo(root.get("value"), filter.getMaxValue())
                );
            }

            if (filter.getDateFrom() != null) {
                predicates.add(
                    cb.greaterThanOrEqualTo(root.get("createdAt"), filter.getDateFrom())
                );
            }

            if (filter.getDateTo() != null) {
                predicates.add(
                    cb.lessThanOrEqualTo(root.get("createdAt"), filter.getDateTo())
                );
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }


}
