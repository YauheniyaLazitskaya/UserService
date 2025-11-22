package com.controllers;

import com.dto.paymentCardsDto.*;
import com.services.PaymentCardService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/cards")
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    public PaymentCardController(PaymentCardService paymentCardService) {
        this.paymentCardService = paymentCardService;
    }

    @PostMapping
    public ResponseEntity<PaymentCardDTO> createPaymentCard(@PathVariable Integer userId,
            @Valid @RequestBody CreatePaymentCardDTO createPaymentCardDTO) {
        PaymentCardDTO paymentCardDTO = paymentCardService.createPaymentCard(userId, createPaymentCardDTO);
        return new ResponseEntity<>(paymentCardDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{cardId}")
    public ResponseEntity<PaymentCardDTO> updatePaymentCard(@PathVariable Integer userId,
                                                            @PathVariable Integer cardId,
                                @Valid @RequestBody UpdatePaymentCardDTO newPaymentCardDTO) {
        PaymentCardDTO paymentCardDTO = paymentCardService.updatePaymentCard(userId, cardId, newPaymentCardDTO);
        return ResponseEntity.ok(paymentCardDTO);
    }

    @GetMapping("/{cardId}")
    public ResponseEntity<PaymentCardDTO> getPaymentCard(@PathVariable Integer userId,
                                                         @PathVariable Integer cardId) {
        PaymentCardDTO paymentCardDTO = paymentCardService.getPaymentCardById(userId, cardId);
        return ResponseEntity.ok(paymentCardDTO);
    }

    @GetMapping
    public ResponseEntity<List<PaymentCardDTO>> getPaymentCardsByUser(@PathVariable Integer userId) {
        List<PaymentCardDTO> userCards = paymentCardService.getPaymentCardsByUserId(userId);
        return ResponseEntity.ok(userCards);
    }

    @GetMapping(path = "/all")
    public ResponseEntity<Page<PaymentCardDTO>> getAllPaymentCards(@RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        Page<PaymentCardDTO> paymentCardDTOPage = paymentCardService.getAllPaymentCards(page, size);
        return ResponseEntity.ok(paymentCardDTOPage);
    }

    @PutMapping("/{cardId}/activity")
    public ResponseEntity<PaymentCardDTO> setActivityCard(@PathVariable Integer userId,
                                                          @PathVariable Integer cardId,
                                                          @RequestParam Boolean status){
        PaymentCardDTO paymentCardDTO = paymentCardService.setActivityCardById(userId, cardId, status);
        return ResponseEntity.ok(paymentCardDTO);
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<Void> deletePaymentCard(@PathVariable Integer userId,
                                                  @PathVariable Integer cardId) {
        paymentCardService.deletePaymentCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
