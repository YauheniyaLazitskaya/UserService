package com.integrationTests;

import com.dto.paymentCardsDto.CreatePaymentCardDTO;
import com.dto.paymentCardsDto.PaymentCardDTO;
import com.dto.paymentCardsDto.UpdatePaymentCardDTO;
import com.entities.PaymentCard;
import com.entities.User;
import com.repositories.PaymentCardRepository;
import com.repositories.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PaymentCardControllerIT extends BaseIT {
    private static final ParameterizedTypeReference<RestPageImpl<PaymentCardDTO>> CARD_PAGE_TYPE =
            new ParameterizedTypeReference<>() {};
    private static final ParameterizedTypeReference<List<PaymentCardDTO>> CARD_LIST_TYPE =
            new ParameterizedTypeReference<>() {};

    private final TestRestTemplate restTemplate;
    private final UserRepository userRepository;
    private final PaymentCardRepository paymentCardRepository;

    @Autowired
    public PaymentCardControllerIT(TestRestTemplate restTemplate,
                                   UserRepository userRepository,
                                   PaymentCardRepository paymentCardRepository) {
        this.restTemplate = restTemplate;
        this.paymentCardRepository = paymentCardRepository;
        this.userRepository = userRepository;
    }

    private User testUser;

    @BeforeEach
    void setUp() {
        paymentCardRepository.deleteAll();
        userRepository.deleteAll();

        testUser = createUserInDb("Be", "Bebe", "bebebe@example.com");
    }

    private User createUserInDb(String name, String surname, String email) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setBirthDate(LocalDate.of(2000, 12, 20));
        user.setActive(true);
        return userRepository.save(user);
    }

    private PaymentCard createCardInDb(User owner, String number) {
        PaymentCard card = new PaymentCard();
        card.setNumber(number);
        card.setHolder(owner.getName().toUpperCase() + " " + owner.getSurname().toUpperCase());
        card.setExpirationDate(LocalDate.now().plusYears(5));
        card.setActive(true);
        card.setUser(owner);
        return paymentCardRepository.save(card);
    }


    @Test
    void createPaymentCardSuccess() {
        CreatePaymentCardDTO newCardDto = new CreatePaymentCardDTO();
        newCardDto.setNumber("1111222233334444");
        newCardDto.setHolder("PU PUPU");
        newCardDto.setExpirationDate(LocalDate.now().plusYears(2));

        String url = "/api/users/" + testUser.getId() + "/cards";
        ResponseEntity<PaymentCardDTO> response = restTemplate.postForEntity(url, newCardDto, PaymentCardDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        PaymentCardDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals("1111222233334444", responseBody.getNumber());
        assertTrue(paymentCardRepository.findById(responseBody.getId()).isPresent());
    }

    @Test
    void getPaymentCardSuccess() {
        PaymentCard card = createCardInDb(testUser, "5555666677778888");

        String url = "/api/users/" + testUser.getId() + "/cards/" + card.getId();
        ResponseEntity<PaymentCardDTO> response = restTemplate.getForEntity(url, PaymentCardDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentCardDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(card.getId(), responseBody.getId());
        assertEquals(testUser.getId(), responseBody.getUserId());
    }

    @Test
    void getPaymentCardsByUserSuccess() {
        createCardInDb(testUser, "1000100010001000");
        createCardInDb(testUser, "2000200020002000");
        User otherUser = createUserInDb("No", "Nono", "nonono@test.com");
        createCardInDb(otherUser, "9999999999999999");

        String url = "/api/users/" + testUser.getId() + "/cards";
        ResponseEntity<List<PaymentCardDTO>> response = restTemplate.exchange(url, HttpMethod.GET,
                null, CARD_LIST_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        List<PaymentCardDTO> cards = response.getBody();
        assertNotNull(cards);
        assertEquals(2, cards.size());
    }

    @Test
    void updatePaymentCardSuccess() {
        PaymentCard card = createCardInDb(testUser, "3000300030003000");
        UpdatePaymentCardDTO updateDto = new UpdatePaymentCardDTO();
        updateDto.setHolder("Mo Momo");

        String url = "/api/users/" + testUser.getId() + "/cards/" + card.getId();
        HttpEntity<UpdatePaymentCardDTO> request = new HttpEntity<>(updateDto);
        ResponseEntity<PaymentCardDTO> response = restTemplate.exchange(url, HttpMethod.PUT,
                request, PaymentCardDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentCardDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Mo Momo", responseBody.getHolder());

        PaymentCard updatedCardInDb = paymentCardRepository.findById(card.getId()).orElseThrow();
        assertEquals("Mo Momo", updatedCardInDb.getHolder());
    }

    @Test
    void getAllPaymentCardsSuccess() {
        createCardInDb(testUser, "4000400040004000");
        User otherUser = createUserInDb("Mi", "Mimi", "mimimi@test.com");
        createCardInDb(otherUser, "5000500050005000");

        String url = "/api/users/" + testUser.getId() + "/cards/all?page=0&size=5";

        ResponseEntity<RestPageImpl<PaymentCardDTO>> response = restTemplate.exchange(url,
                HttpMethod.GET, null, CARD_PAGE_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<PaymentCardDTO> page = response.getBody();
        assertNotNull(page);
        assertEquals(2, page.getTotalElements());
    }

    @Test
    void setActivityCardSuccess() {
        PaymentCard card = createCardInDb(testUser, "6000600060006000");
        assertTrue(card.isActive()); // Начальное состояние

        String url = UriComponentsBuilder.fromPath("/api/users/" + testUser.getId() + "/cards/" +
                        card.getId() + "/activity").queryParam("status", false).toUriString();

        ResponseEntity<PaymentCardDTO> response = restTemplate.exchange(url, HttpMethod.PUT,
                null, PaymentCardDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        PaymentCardDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isActive());

        PaymentCard cardInDb = paymentCardRepository.findById(card.getId()).orElseThrow();
        assertFalse(cardInDb.isActive());
    }

    @Test
    void deletePaymentCardSuccess() {
        PaymentCard card = createCardInDb(testUser, "7000700070007000");

        String url = "/api/users/" + testUser.getId() + "/cards/" + card.getId();
        ResponseEntity<Void> response = restTemplate.exchange(url, HttpMethod.DELETE,
                null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(paymentCardRepository.findById(card.getId()).isPresent());
    }
}