package com.entities;

import com.audit.AuditableEntity;
import com.exceptions.PaymentCardException;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@ToString
@Entity
@Table(name="users")
public class User extends AuditableEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "name", nullable = false, length = 20)
    private String name;

    @Column(name = "surname", nullable = false, length = 30)
    private String surname;

    @Column(name = "birth_date", nullable = false)
    private Date birthDate;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Column(name = "active", nullable = false)
    private boolean active;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
    private List<PaymentCard> paymentCards = new ArrayList<>();

    public User() {}

    public User(String name, String surname, Date birthDate, String email, boolean active) {
        this.name = name;
        this.surname = surname;
        this.birthDate = birthDate;
        this.email = email;
        this.active = active;
    }

    public void addPaymentCard(PaymentCard card) {
        if(paymentCards.size()>5)
            throw new PaymentCardException("The card owner already has 5 cards. Creating a new card is not possible.");
        paymentCards.add(card);
        card.setUser(this);
    }

    public void removePaymentCard(PaymentCard card) {
        paymentCards.remove(card);
    }
}
