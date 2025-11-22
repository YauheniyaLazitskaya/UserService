package com.dto.paymentCardsDto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class PaymentCardFilterDTO {
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private Boolean active;
}
