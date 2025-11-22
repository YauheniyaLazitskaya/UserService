package com.mappers;

import com.dto.paymentCardsDto.*;
import com.entities.PaymentCard;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PaymentCardMapper {
    @Mapping(source = "user.id", target = "userId")
    PaymentCardDTO toDto(PaymentCard paymentCard);
    PaymentCard toEntity(PaymentCardDTO paymentCardDTO);
    PaymentCard toEntity(CreatePaymentCardDTO createPaymentCardDTO);
    List<PaymentCardDTO> toDtoList(List<PaymentCard> paymentCards);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePaymentCardFromDto(UpdatePaymentCardDTO newPaymentCardDTO,
                                  @MappingTarget PaymentCard oldPaymentCard);
}
