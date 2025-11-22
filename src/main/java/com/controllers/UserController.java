package com.controllers;

import com.dto.usersDto.*;
import com.services.UserService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
        UserDTO userDTO = userService.createUser(createUserDTO);
        return new ResponseEntity<>(userDTO, HttpStatus.CREATED);
    }

    @PutMapping("/{userId}")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Integer userId,
                                              @Valid @RequestBody UpdateUserDTO newUserDTO){
            UserDTO userDTO = userService.updateUserById(userId, newUserDTO);
            return ResponseEntity.ok(userDTO);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#id, authentication)")
    public ResponseEntity<UserDTO> getUser(@PathVariable Integer id){
        UserDTO userDTO = userService.getUserById(id);
        return ResponseEntity.ok(userDTO);
    }

    @GetMapping
    @PreAuthorize("hasRole('admin')")
    public ResponseEntity<Page<UserDTO>> getAllUsers(@ModelAttribute UserFilterDTO filter,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "10") int size){
        Page<UserDTO> userDTOPage = userService.getAllUsers(filter, page, size);
        return ResponseEntity.ok(userDTOPage);
    }

    @PutMapping("/{userId}/activity")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#userId, authentication)")
    public ResponseEntity<UserDTO> setActivityUser(@PathVariable Integer userId,
                                                   @RequestParam Boolean status){
        UserDTO userDTO = userService.setActivityUserById(userId, status);
        return ResponseEntity.ok(userDTO);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('admin') or @securityCheck.isUserOwner(#id, authentication)")
    public ResponseEntity<Void> deleteUser(@PathVariable Integer id){
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
