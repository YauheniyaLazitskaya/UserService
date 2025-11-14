package com.unitTests;

import com.dto.paymentCardsDto.*;
import com.entities.*;
import com.exceptions.*;
import com.mappers.*;
import com.repositories.*;
import com.services.PaymentCardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.junit.jupiter.api.Test;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class PaymentCardServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PaymentCardRepository paymentCardRepository;
    @Mock
    private PaymentCardMapper paymentCardMapper;

    @InjectMocks
    private PaymentCardService paymentCardService;

    private User user;
    private PaymentCard paymentCard;
    private PaymentCardDTO paymentCardDTO;
    private CreatePaymentCardDTO createPaymentCardDTO;
    private UpdatePaymentCardDTO updatePaymentCardDTO;

    @BeforeEach
    public void setUp() {
        user = new User();
        user.setId(1);

        paymentCard = new PaymentCard();
        paymentCard.setId(1);
        paymentCard.setNumber("1234567812345678");
        paymentCard.setUser(user);

        paymentCardDTO = new PaymentCardDTO();
        paymentCardDTO.setId(1);
        paymentCardDTO.setNumber("1234567812345678");
        paymentCardDTO.setUserId(user.getId());

        createPaymentCardDTO = new CreatePaymentCardDTO();
        createPaymentCardDTO.setNumber("1234567812345678");

        updatePaymentCardDTO = new UpdatePaymentCardDTO();
        updatePaymentCardDTO.setNumber("8765432187654321");
    }

    @Test
    void createPaymentCardSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toEntity(createPaymentCardDTO)).thenReturn(paymentCard);
        when(paymentCardRepository.findByNumber(paymentCard.getNumber())).thenReturn(Optional.empty());
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(paymentCard);
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(paymentCardDTO);

        PaymentCardDTO result = paymentCardService.createPaymentCard(1, createPaymentCardDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(paymentCardRepository).save(any(PaymentCard.class));
    }

    @Test
    void createPaymentCardPaymentCardException() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardMapper.toEntity(createPaymentCardDTO)).thenReturn(paymentCard);
        when(paymentCardRepository.findByNumber(paymentCard.getNumber())).thenReturn(Optional.of(new PaymentCard()));

        PaymentCardException exception = assertThrows(PaymentCardException.class, () -> {
            paymentCardService.createPaymentCard(1, createPaymentCardDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(paymentCardRepository, never()).save(any(PaymentCard.class));
    }

    @Test
    void updatePaymentCardSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findById(1)).thenReturn(Optional.of(paymentCard));
        when(paymentCardRepository.findByNumber(updatePaymentCardDTO.getNumber())).thenReturn(Optional.empty());
        paymentCard.setNumber(updatePaymentCardDTO.getNumber());
        paymentCardDTO.setNumber(updatePaymentCardDTO.getNumber());
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(paymentCard);
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(paymentCardDTO);

        PaymentCardDTO result = paymentCardService.updatePaymentCard(1, 1, updatePaymentCardDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals(updatePaymentCardDTO.getNumber(), result.getNumber());
        verify(paymentCardRepository).save(paymentCard);
    }

    @Test
    void getPaymentCardByIdSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findById(1)).thenReturn(Optional.of(paymentCard));
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(paymentCardDTO);

        PaymentCardDTO result = paymentCardService.getPaymentCardById(1, 1);

        assertNotNull(result);
        assertEquals(paymentCardDTO.getId(), result.getId());
        assertEquals(paymentCardDTO.getNumber(), result.getNumber());
        assertEquals(paymentCardDTO.getUserId(), result.getUserId());
    }

    @Test
    void getPaymentCardsByUserIDSuccess() {
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findCardsByUserId(1)).thenReturn(List.of(paymentCard));
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(paymentCardDTO);

        List<PaymentCardDTO> result = paymentCardService.getPaymentCardsByUserId(1);

        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(paymentCardDTO.getId(), result.get(0).getId());
    }

    @Test
    void getAllPaymentCardsSuccess() {
        Page<PaymentCard> page = new PageImpl<>(List.of(paymentCard));
        when(paymentCardRepository.findAll(any(PageRequest.class))).thenReturn(page);
        when(paymentCardMapper.toDto(any(PaymentCard.class))).thenReturn(paymentCardDTO);

        Page<PaymentCardDTO> result = paymentCardService.getAllPaymentCards(0, 10);

        assertFalse(result.isEmpty());
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void setActivityCardByIdSuccess() {
        paymentCard.setActive(false);
        paymentCardDTO.setActive(false);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findById(1)).thenReturn(Optional.of(paymentCard));
        paymentCard.setActive(true);
        paymentCardDTO.setActive(true);
        when(paymentCardRepository.save(any(PaymentCard.class))).thenReturn(paymentCard);
        when(paymentCardMapper.toDto(paymentCard)).thenReturn(paymentCardDTO);

        PaymentCardDTO result = paymentCardService.setActivityCardById(1, 1, true);

        assertNotNull(result);
        assertTrue(result.isActive());
        verify(paymentCardRepository).save(paymentCard);
    }

    @Test
    void deletePaymentCardSuccess() {
        user.getPaymentCards().add(paymentCard);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findById(1)).thenReturn(Optional.of(paymentCard));

        assertDoesNotThrow(() -> paymentCardService.deletePaymentCard(1, 1));

        assertFalse(user.getPaymentCards().contains(paymentCard));
    }

    @Test
    void deletePaymentPaymentCardException() {
        User userOwner = new User();
        userOwner.setId(2);
        paymentCard.setUser(userOwner);
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(paymentCardRepository.findById(1)).thenReturn(Optional.of(paymentCard));

        PaymentCardException exception = assertThrows(PaymentCardException.class, ()
                -> paymentCardService.deletePaymentCard(1, 1));
        assertTrue(exception.getMessage().contains("doesn't have"));
    }
}
