package com.controllers;

import com.dto.paymentCardsDto.*;
import com.services.PaymentCardService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users/{userId}/cards")
@RequiredArgsConstructor
public class PaymentCardController {
    private final PaymentCardService paymentCardService;

    @PostMapping
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<PaymentCardDTO> createPaymentCard(@PathVariable Integer userId,
            @Valid @RequestBody CreatePaymentCardDTO createPaymentCardDTO) {
        PaymentCardDTO paymentCardDTO = paymentCardService.createPaymentCard(userId, createPaymentCardDTO);
        return new ResponseEntity<>(paymentCardDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{cardId}")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<PaymentCardDTO> updatePaymentCard(@PathVariable Integer userId,
                                                            @PathVariable Integer cardId,
                                @Valid @RequestBody UpdatePaymentCardDTO newPaymentCardDTO) {
        PaymentCardDTO paymentCardDTO = paymentCardService.updatePaymentCard(userId, cardId, newPaymentCardDTO);
        return ResponseEntity.ok(paymentCardDTO);
    }

    @GetMapping("/{cardId}")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<PaymentCardDTO> getPaymentCard(@PathVariable Integer userId,
                                                         @PathVariable Integer cardId) {
        PaymentCardDTO paymentCardDTO = paymentCardService.getPaymentCardById(userId, cardId);
        return ResponseEntity.ok(paymentCardDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<List<PaymentCardDTO>> getPaymentCardsByUser(@PathVariable Integer userId) {
        List<PaymentCardDTO> userCards = paymentCardService.getPaymentCardsByUserId(userId);
        return ResponseEntity.ok(userCards);
    }

    @GetMapping(path = "/all")
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<PaymentCardDTO>> getAllPaymentCards(@ModelAttribute PaymentCardFilterDTO filter,
                                                                   @RequestParam(defaultValue = "0") int page,
                                                                   @RequestParam(defaultValue = "10") int size){
        Page<PaymentCardDTO> paymentCardDTOPage = paymentCardService.getAllPaymentCards(filter, page, size);
        return ResponseEntity.ok(paymentCardDTOPage);
    }

    @PutMapping("/{cardId}/activity")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<PaymentCardDTO> setActivityCard(@PathVariable Integer userId,
                                                          @PathVariable Integer cardId,
                                                          @RequestParam Boolean status){
        PaymentCardDTO paymentCardDTO = paymentCardService.setActivityCardById(userId, cardId, status);
        return ResponseEntity.ok(paymentCardDTO);
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<Void> deletePaymentCard(@PathVariable Integer userId,
                                                  @PathVariable Integer cardId) {
        paymentCardService.deletePaymentCard(userId, cardId);
        return ResponseEntity.noContent().build();
    }
}
