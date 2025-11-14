package com.dto.paymentCardsDto;

import com.entities.User;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class CreatePaymentCardDTO {
    @NotBlank(message = "Number of card can't be empty.")
    @Size(max = 16, min = 16, message = "Name must be 16 characters.")
    private String number;

    @NotBlank(message = "Name of holder can't be empty.")
    @Size(max = 60, min = 5, message = "Name must be more than 5 and less than 60 characters.")
    private String holder;

    @NotNull(message = "Expiration date can't be null.")
    @Future(message = "Expiration date must be in the future.")
    private LocalDate expirationDate;

    private boolean active = true;
}
