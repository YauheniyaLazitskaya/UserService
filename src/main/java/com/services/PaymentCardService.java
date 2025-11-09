package com.services;

import com.entities.PaymentCard;
import com.entities.User;
import com.exceptions.*;
import com.repositories.PaymentCardRepository;
import com.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentCardService {
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;

    public PaymentCardService(PaymentCardRepository paymentCardRepository, UserRepository userRepository) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
    }

    public PaymentCard createPaymentCard(PaymentCard paymentCard) {
        if(paymentCard.getUser().getPaymentCards().size()>5)
            throw new PaymentCardException("The card owner already has 5 cards. Creating a new card is not possible.");
        if(paymentCardRepository.findByNumber(paymentCard.getNumber()).isPresent())
            throw new PaymentCardException("Payment card with this number ("
                    + paymentCard.getNumber() + ") already exists.");
        return paymentCardRepository.save(paymentCard);
    }

    public PaymentCard updatePaymentCard(PaymentCard paymentCard) {
        if(paymentCardRepository.findById(paymentCard.getId()).isEmpty())
            throw new PaymentCardException("Payment card with this id ("
                    + paymentCard.getId() + ") doesn't exist.");
        return paymentCardRepository.save(paymentCard);
    }

    public PaymentCard getPaymentCardById(Integer id) {
        return paymentCardRepository.findById(id).orElseThrow( ()
                -> new  PaymentCardException("Payment card with this id (" + id + ") doesn't exist."));
    }

    public List<PaymentCard> getPaymentCardsByUserID(Integer id){
        if(userRepository.findById(id).isEmpty())
                throw new UserException("User not found with id: " + id);
        return paymentCardRepository.findCardsByUserId(id);
    }

    public Page<PaymentCard> getAllPaymentCards(int page, int size){
        return paymentCardRepository.findAll(PageRequest.of(page, size));
    }

    public PaymentCard setActivityCardByID(Integer id, Boolean status){
        PaymentCard paymentCard = paymentCardRepository.findById(id).orElseThrow(()
                -> new PaymentCardException("PaymentCard not found with id: " + id));
        paymentCard.setActive(status);
        return paymentCardRepository.save(paymentCard);
    }

    public void deletePaymentCard(Integer id) {
        if(paymentCardRepository.findById(id).isEmpty())
            throw new PaymentCardException("Payment card with this id (" + id + ") doesn't exist.");
        paymentCardRepository.deleteById(id);
    }
}
