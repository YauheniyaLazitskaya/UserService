package com.dto.paymentCardsDto;

import lombok.Data;
import java.io.Serializable;
import java.time.LocalDate;

@Data
public class PaymentCardDTO implements Serializable {
    private Integer id;
    private String number;
    private String holder;
    private LocalDate expirationDate;
    private boolean active;
    private Integer userId;
}
