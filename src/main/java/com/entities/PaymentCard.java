package com.entities;

import com.entities.audit.AuditableEntity;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@ToString(exclude = "user")
@Entity
@Table(name = "payment_cards")
public class PaymentCard extends AuditableEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "number", nullable = false, unique = true, length = 16)
    private String number;

    @Column(name = "holder", nullable = false, length = 60)
    private String holder;

    @Column(name = "expiration_date", nullable = false)
    private LocalDate expirationDate;

    @Column(name = "active", nullable = false)
    private boolean active;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public PaymentCard() {}

    public PaymentCard(String number, String holder, LocalDate expirationDate, boolean active) {
        this.number = number;
        this.holder = holder;
        this.expirationDate = expirationDate;
        this.active = active;
    }
}
