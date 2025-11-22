package com.services;

import com.dto.paymentCardsDto.*;
import com.entities.*;
import com.exceptions.PaymentCardException;
import com.exceptions.UserException;
import com.mappers.PaymentCardMapper;
import com.repositories.*;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class PaymentCardService {
    private final PaymentCardRepository paymentCardRepository;
    private final UserRepository userRepository;
    private final PaymentCardMapper paymentCardMapper;

    public PaymentCardService(PaymentCardRepository paymentCardRepository,
                              UserRepository userRepository, PaymentCardMapper paymentCardMapper) {
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
        this.paymentCardMapper = paymentCardMapper;
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public PaymentCardDTO createPaymentCard(Integer userId, CreatePaymentCardDTO createPaymentCardDTO) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        if(user.getPaymentCards().size()>5)
            throw new PaymentCardException("The card owner already has 5 cards. Creating a new card is not possible.");
        PaymentCard paymentCard = paymentCardMapper.toEntity(createPaymentCardDTO);
        if(paymentCardRepository.findByNumber(paymentCard.getNumber()).isPresent())
            throw new PaymentCardException("Payment card with this number ("
                    + paymentCard.getNumber() + ") already exists.");
        paymentCard.setUser(user);
        PaymentCard savedCard = paymentCardRepository.save(paymentCard);
        return paymentCardMapper.toDto(savedCard);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public PaymentCardDTO updatePaymentCard(Integer userId, Integer cardId, UpdatePaymentCardDTO newPaymentCardDTO) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        PaymentCard cardToUpdate = paymentCardRepository.findById(cardId).orElseThrow(()
                -> new PaymentCardException("Payment card not found with id: " + cardId));
        if(!user.getId().equals(cardToUpdate.getUser().getId()))
            throw new PaymentCardException("The user with id = " + userId
                    + " doesn't have a card with id = " + cardId);
        if(newPaymentCardDTO.getNumber()!=null){
            Optional<PaymentCard> existingCardOptional = paymentCardRepository.
                    findByNumber(newPaymentCardDTO.getNumber());
            if(existingCardOptional.isPresent())
                if(!existingCardOptional.get().getId().equals(cardId))
                    throw new PaymentCardException("Payment card with this number ("
                            + newPaymentCardDTO.getNumber() + ") already exists.");
        }
        paymentCardMapper.updatePaymentCardFromDto(newPaymentCardDTO, cardToUpdate);
        PaymentCard updatedCard = paymentCardRepository.save(cardToUpdate);
        return paymentCardMapper.toDto(updatedCard);
    }


    @Transactional(readOnly = true)
    public PaymentCardDTO getPaymentCardById(Integer userId, Integer cardId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow( ()
                -> new  PaymentCardException("Payment card not found with id: " + cardId));
        if(!user.getId().equals(paymentCard.getUser().getId()))
            throw new PaymentCardException("The user with id = " + userId
                    + " doesn't have a card with id = " + cardId);
        return paymentCardMapper.toDto(paymentCard);
    }

    @Transactional(readOnly = true)
    public List<PaymentCardDTO> getPaymentCardsByUserId(Integer userId){
        if(userRepository.findById(userId).isEmpty())
                throw new UserException("User not found with id: " + userId);
        List<PaymentCard> paymentCards = paymentCardRepository.findCardsByUserId(userId);
        return paymentCards.stream().map(paymentCardMapper::toDto).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public Page<PaymentCardDTO> getAllPaymentCards(int page, int size){
        return paymentCardRepository.findAll(PageRequest.of(page, size)).map(paymentCardMapper::toDto);
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public PaymentCardDTO setActivityCardById(Integer userId, Integer cardId, Boolean status){
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(()
                -> new PaymentCardException("PaymentCard not found with id: " + cardId));
        if(!user.getId().equals(paymentCard.getUser().getId()))
            throw new PaymentCardException("The user with id = " + userId
                    + " doesn't have a card with id = " + cardId);
        paymentCard.setActive(status);
        return paymentCardMapper.toDto(paymentCardRepository.save(paymentCard));
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deletePaymentCard(Integer userId, Integer cardId) {
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        PaymentCard paymentCard = paymentCardRepository.findById(cardId).orElseThrow(()
                -> new PaymentCardException("PaymentCard not found with id: " + cardId));
        if(!user.getId().equals(paymentCard.getUser().getId()))
            throw new PaymentCardException("The user with id = " + userId
                    + " doesn't have a card with id = " + cardId);
        user.removePaymentCard(paymentCard);
    }
}
