package com.specifications;

import com.entities.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> hasName(String name) {
        return (root, query, criteriaBuilder) -> {
            if (name == null || name.isBlank()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("name")),
                    "%" + name.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasSurname(String surname) {
        return (root, query, criteriaBuilder) -> {
            if (surname == null || surname.isBlank()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("surname")),
                    "%" + surname.toLowerCase() + "%");
        };
    }

    public static Specification<User> hasEmail(String email) {
        return (root, query, criteriaBuilder) -> {
            if (email == null || email.isBlank()) return null;
            return criteriaBuilder.like(criteriaBuilder.lower(root.get("email")),
                    "%" +  email.toLowerCase() + "%");
        };
    }

    public static Specification<User> isActive(Boolean active) {
        return (root, query, criteriaBuilder) -> {
            if (active == null) return null;
            return criteriaBuilder.equal(root.get("active"), active);
        };
    }
}
