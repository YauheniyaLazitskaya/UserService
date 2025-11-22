package com.dto.usersDto;
import com.dto.paymentCardsDto.PaymentCardDTO;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDTO implements Serializable {
    private Integer id;
    private String name;
    private String surname;
    private String email;
    private LocalDate birthDate;
    private boolean active;
    private List<PaymentCardDTO> paymentCards;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
