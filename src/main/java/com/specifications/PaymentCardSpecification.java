package com.specifications;

import com.entities.PaymentCard;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class PaymentCardSpecification {
    public static Specification<PaymentCard> hasNumber(String number) {
        return (root, query, criteriaBuilder) -> {
            if (number == null || number.isBlank()) return null;
            return criteriaBuilder.like(root.get("number"), "%" + number + "%");
        };
    }

    public static Specification<PaymentCard> hasHolder(String holder) {
        return (root, query, criteriaBuilder) -> {
            if (holder == null || holder.isBlank()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("holder")),
                    "%" + holder.toLowerCase() + "%");
        };
    }

    public static Specification<PaymentCard> hasExpirationDate(LocalDate date) {
        return (root, query, criteriaBuilder) -> {
            if (date == null) return null;
            return criteriaBuilder.equal(root.get("expirationDate"), date);
        };
    }

    public static Specification<PaymentCard> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) return null;
            return criteriaBuilder.equal(root.get("active"), active);
        };
    }
}
