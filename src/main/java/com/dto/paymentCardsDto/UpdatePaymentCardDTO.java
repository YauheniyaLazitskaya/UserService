package com.dto.paymentCardsDto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

@Data
public class UpdatePaymentCardDTO {
    @Size(max = 16, min = 16, message = "Name must be 16 characters.")
    private String number;

    @Size(max = 60, min = 5, message = "Name must be more than 5 and less than 60 characters.")
    private String holder;

    @Future(message = "Expiration date must be in the future.")
    private LocalDate expirationDate;
}
