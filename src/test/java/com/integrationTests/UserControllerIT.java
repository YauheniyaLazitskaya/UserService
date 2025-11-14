package com.integrationTests;

import com.RestPageImpl;
import com.dto.usersDto.*;
import com.entities.User;
import com.repositories.*;
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
import static org.junit.jupiter.api.Assertions.*;


public class UserControllerIT extends BaseIT {
    private final TestRestTemplate restTemplate;
    private final UserRepository userRepository;
    private static final ParameterizedTypeReference<RestPageImpl<UserDTO>> USER_PAGE_TYPE =
            new ParameterizedTypeReference<>() {};

    @Autowired
    UserControllerIT(TestRestTemplate restTemplate, UserRepository userRepository) {
        this.restTemplate = restTemplate;
        this.userRepository = userRepository;
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private User createUserInDatabase(String name, String surname, String email) {
        User user = new User();
        user.setName(name);
        user.setSurname(surname);
        user.setEmail(email);
        user.setActive(true);
        user.setBirthDate(LocalDate.of(2000, 12, 20));
        return userRepository.save(user);
    }

    @Test
    void createUser() {
        CreateUserDTO newUser = new CreateUserDTO();
        newUser.setName("Pu");
        newUser.setSurname("Pupu");
        newUser.setBirthDate(java.time.LocalDate.of(2000, 12, 20));
        newUser.setEmail("pupupu@test.com");

        ResponseEntity<UserDTO> response = restTemplate.postForEntity("/api/users", newUser, UserDTO.class);

        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertNotNull(responseBody.getId());
        assertEquals("Pu", responseBody.getName());
        assertTrue(userRepository.findById(responseBody.getId()).isPresent());
    }

    @Test
    void getUserByIdSuccess() {
        User existingUser = createUserInDatabase("Pi", "Pipi", "pipipi@test.com");

        ResponseEntity<UserDTO> response = restTemplate.getForEntity("/api/users/" + existingUser.getId(), UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals(existingUser.getId(), responseBody.getId());
        assertEquals("Pi", responseBody.getName());
    }

    @Test
    void getUserByIdNotFound() {
        ResponseEntity<UserDTO> response = restTemplate.getForEntity("/api/users/999", UserDTO.class);
        assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
    }

    @Test
    void updateUser() {
        User existingUser = createUserInDatabase("Pi", "Pipi", "pipipi@test.com");
        UpdateUserDTO updateUserDTO = new UpdateUserDTO();
        updateUserDTO.setName("Be");
        updateUserDTO.setEmail("bebebe@test.com");

        HttpEntity<UpdateUserDTO> requestEntity = new HttpEntity<>(updateUserDTO);
        ResponseEntity<UserDTO> response = restTemplate.exchange("/api/users/" + existingUser.getId(),
                HttpMethod.PUT, requestEntity, UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertEquals("Be", responseBody.getName());
        assertEquals("bebebe@test.com", responseBody.getEmail());

        User updatedUserInDb = userRepository.findById(existingUser.getId()).orElseThrow();
        assertEquals("Be", updatedUserInDb.getName());
    }

    @Test
    void getAllUsers() {
        createUserInDatabase("Be", "Bebe", "bebebe@test.com");
        createUserInDatabase("Pi", "Pipi", "pipipi@test.com");

        ResponseEntity<RestPageImpl<UserDTO>> response = restTemplate.exchange("/api/users?page=0&size=10",
                HttpMethod.GET, null, USER_PAGE_TYPE);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        Page<UserDTO> userPage = response.getBody();
        assertNotNull(userPage);
        assertEquals(2, userPage.getTotalElements());
        assertEquals(1, userPage.getTotalPages());
        assertEquals(2, userPage.getContent().size());
    }

    @Test
    void setActivityUser() {
        User existingUser = createUserInDatabase("Pi", "Pipi", "activity@test.com");
        assertTrue(existingUser.isActive());

        String url = UriComponentsBuilder.fromPath("/api/users/" + existingUser.getId() + "/activity")
                .queryParam("status", "false").toUriString();

        ResponseEntity<UserDTO> response = restTemplate.exchange(url, HttpMethod.PUT, null,
                UserDTO.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        UserDTO responseBody = response.getBody();
        assertNotNull(responseBody);
        assertFalse(responseBody.isActive());

        User userInDb = userRepository.findById(existingUser.getId()).orElseThrow();
        assertFalse(userInDb.isActive());
    }

    @Test
    void deleteUser() {
        User existingUser = createUserInDatabase("Pi", "Pipi", "activity@test.com");

        ResponseEntity<Void> response = restTemplate.exchange("/api/users/" + existingUser.getId(),
                HttpMethod.DELETE, null, Void.class);

        assertEquals(HttpStatus.NO_CONTENT, response.getStatusCode());
        assertFalse(userRepository.findById(existingUser.getId()).isPresent());
    }
}
