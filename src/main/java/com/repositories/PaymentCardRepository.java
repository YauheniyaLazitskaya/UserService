package com.repositories;

import com.entities.PaymentCard;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PaymentCardRepository extends JpaRepository<PaymentCard, Integer> {
    @Query(value = "SELECT * FROM payment_cards WHERE user_id = :id", nativeQuery = true)
    List<PaymentCard> findCardsByUserId(@Param("id") Integer id);

    @Query("SELECT pc FROM PaymentCard pc WHERE pc.number = :number")
    Optional<PaymentCard> findByNumber(@Param("number") String cardNumber);
}
