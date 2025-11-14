package com.controllers;

import com.dto.usersDto.*;
import com.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody CreateUserDTO createUserDTO){
        UserDTO userDAO = userService.createUser(createUserDTO);
        return new ResponseEntity<>(userDAO, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer userId,
                                              @Valid @RequestBody UpdateUserDTO newUserDTO){
        try {
            UserDTO userDTO = userService.updateUserById(userId, newUserDTO);
            return ResponseEntity.ok(userDTO);
        }catch (Exception e){
            System.out.println("------------------------------ОШИБКА-----------------------------");
            e.printStackTrace();
            System.out.println("-------------------------------КОНЕЦ------------------------------");
        }
        return null;
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id){
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    public ResponseEntity<Page<UserDTO>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        Page<UserDTO> userDTOPage = userService.getAllUsers(page, size);
        return ResponseEntity.ok(userDTOPage);
    }

    @PutMapping("/{userId}/activity")
    public ResponseEntity<UserDTO> setActivityUser(@PathVariable Integer userId,
                                                   @RequestParam Boolean status){
        UserDTO userDTO = userService.setActivityUserById(userId, status);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
