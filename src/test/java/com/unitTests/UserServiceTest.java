package com.unitTests;

import com.dto.usersDto.CreateUserDTO;
import com.dto.usersDto.UpdateUserDTO;
import com.dto.usersDto.UserDTO;
import com.dto.usersDto.UserFilterDTO;
import com.entities.User;
import com.exceptions.UserException;
import com.mappers.UserMapper;
import com.repositories.UserRepository;
import com.services.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

@Disabled
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private UserService userService;

    private Integer userId;
    private User fakeUser;
    private User fakeSavedUser;
    private CreateUserDTO fakeCreateUserDTO;
    private UserDTO fakeUserDTO;
    private UpdateUserDTO fakeUpdateUserDTO;

    @BeforeEach
    public void setUp() {
        userId = 1;

        fakeCreateUserDTO = new CreateUserDTO();
        fakeCreateUserDTO.setName("Pupupu");
        fakeCreateUserDTO.setEmail("Pupupu@test.com");

        fakeUser = new User();
        fakeUser.setName("Pupupu");
        fakeUser.setEmail("Pupupu@test.com");

        fakeSavedUser = new User();
        fakeSavedUser.setId(1);
        fakeSavedUser.setName("Pupupu");
        fakeSavedUser.setEmail("Pupupu@test.com");

        fakeUserDTO = new UserDTO();
        fakeUserDTO.setId(1);
        fakeUserDTO.setName("Pupupu");
        fakeUserDTO.setEmail("Pupupu@test.com");

        fakeUpdateUserDTO =  new UpdateUserDTO();
        fakeUpdateUserDTO.setName("Huhuhu");
        fakeUpdateUserDTO.setEmail("Pupupu@test.com");
    }

    @Test
    void getUserByIdSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeUser));
        when(userMapper.toDto(fakeUser)).thenReturn(fakeUserDTO);

        UserDTO resultUserDTO = userService.getUserById(userId);

        assertNotNull(resultUserDTO);
        assertEquals(userId, resultUserDTO.getId());
        assertEquals(fakeUser.getName(), resultUserDTO.getName());
    }

    @Test
    void getUserByIDUserException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            userService.getUserById(userId);
        });

        assertEquals("User not found with id: " + userId, exception.getMessage());
    }

    @Test
    void createUserSuccess() {
        when(userMapper.toEntity(fakeCreateUserDTO)).thenReturn(fakeUser);
        when(userRepository.findByEmail(fakeUser.getEmail())).thenReturn(Optional.empty());
        when(userRepository.save(any(User.class))).thenReturn(fakeSavedUser);
        when(userMapper.toDto(fakeSavedUser)).thenReturn(fakeUserDTO);

        UserDTO result = userService.createUser(fakeCreateUserDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Pupupu", result.getName());

        //verify(userRepository, times(1)).save(fakeUser);
    }

    @Test
    void createUserUserException() {
        when(userMapper.toEntity(fakeCreateUserDTO)).thenReturn(fakeUser);
        when(userRepository.findByEmail(fakeCreateUserDTO.getEmail())).thenReturn(Optional.of(fakeSavedUser));

        UserException exception = assertThrows(UserException.class, () -> {
            userService.createUser(fakeCreateUserDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void updateUserByIdSuccess() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeSavedUser));
        when(userRepository.findByEmail(fakeUpdateUserDTO.getEmail())).thenReturn(Optional.of(fakeSavedUser));
        fakeUserDTO.setName("Huhuhu");
        when(userMapper.toDto(fakeSavedUser)).thenReturn(fakeUserDTO);
        when(userRepository.save(any(User.class))).thenReturn(fakeSavedUser);

        UserDTO result = userService.updateUserById(userId, fakeUpdateUserDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("Huhuhu", result.getName());
    }

    @Test
    void updateUserByIdUserException() {
        fakeSavedUser.setEmail("Huhuhu@test.com");
        User anyFakeSavedUser = new User();
        anyFakeSavedUser.setId(2);
        anyFakeSavedUser.setEmail("Pupupu@test.com");


        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeSavedUser));
        when(userRepository.findByEmail(fakeUpdateUserDTO.getEmail())).thenReturn(Optional.of(anyFakeSavedUser));

        UserException exception = assertThrows(UserException.class, () -> {
            userService.updateUserById(userId, fakeUpdateUserDTO);
        });

        assertTrue(exception.getMessage().contains("already exists"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void getAllUsersSuccess() {
        int page = 0;
        int size = 10;
        Sort sort = Sort.by("name").and(Sort.by("surname"));
        Pageable pageable = PageRequest.of(page, size, sort);
        Page<User> userPage = new PageImpl<>(List.of(fakeSavedUser));

        when(userRepository.findAll(pageable)).thenReturn(userPage);
        when(userMapper.toDto(fakeSavedUser)).thenReturn(fakeUserDTO);

        Page<UserDTO> result = userService.getAllUsers(null, page, size);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("Pupupu", result.getContent().get(0).getName());
    }

    @Test
    void setActivityUserByIdSuccess(){
        boolean newStatus = false;

        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeSavedUser));
        when(userRepository.save(any(User.class))).thenReturn(fakeSavedUser);
        when(userMapper.toDto(fakeSavedUser)).thenReturn(fakeUserDTO);

        UserDTO result = userService.setActivityUserById(userId, newStatus);

        assertNotNull(result);
        assertEquals(newStatus, result.isActive());
    }

    @Test
    void setActivityUserByIdUserException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, () -> {
            userService.setActivityUserById(userId, true);
        });

        assertTrue(exception.getMessage().contains("not found"));
        verify(userRepository, never()).save(any(User.class));
    }

    @Test
    void deleteUserByIdSuccess(){
        when(userRepository.findById(userId)).thenReturn(Optional.of(fakeSavedUser));
        doNothing().when(userRepository).deleteById(userId);

        assertDoesNotThrow(() -> { userService.deleteUserById(userId); });
        verify(userRepository, times(1)).deleteById(userId);
    }

    @Test
    void deleteUserByIdUserException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        UserException exception = assertThrows(UserException.class, ()
                -> { userService.deleteUserById(userId); });
        assertTrue(exception.getMessage().contains("not found"));
        verify(userRepository, never()).deleteById(any(Integer.class));
    }
}
