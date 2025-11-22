package com.services;

import com.dto.usersDto.*;
import com.entities.User;
import com.exceptions.UserException;
import com.mappers.UserMapper;
import com.repositories.UserRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;


    public UserService(UserRepository userRepository, UserMapper userMapper) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @Transactional
    public UserDTO createUser(CreateUserDTO createUserDTO){
        User user = userMapper.toEntity(createUserDTO);
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserException("User with this email (" + user.getEmail() + ") already exists.");
        User savedUser = userRepository.save(user);
        return userMapper.toDto(savedUser);
    }

    @CachePut(value = "users", key = "#userId")
    @Transactional
    public UserDTO updateUserById(Integer userId, UpdateUserDTO newUserDTO){
        User userToUpdate = userRepository.findById(userId)
                .orElseThrow(() -> new UserException("User not found with id: " + userId));
        if(!newUserDTO.getEmail().isEmpty()){
            Optional<User> existingUser = userRepository.findByEmail(newUserDTO.getEmail());
            if(existingUser.isPresent())
                if(!existingUser.get().getId().equals(userId))
                    throw new UserException("User with this email (" + newUserDTO.getEmail() + ") already exists");
        }
        userMapper.updateUserFromDto(newUserDTO, userToUpdate);
        User updatedUser = userRepository.save(userToUpdate);
        return userMapper.toDto(updatedUser);
    }

    @Cacheable(value = "users", key = "#userId")
    @Transactional(readOnly = true)
    public UserDTO getUserById(Integer userId){
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        return userMapper.toDto(user);
    }

    @Transactional(readOnly = true)
    public Page<UserDTO> getAllUsers(int page, int size){
        return userRepository.findAll(PageRequest.of(page, size, Sort.by("name").
                and(Sort.by("surname")))).map(userMapper::toDto);
    }

    @CachePut(value = "users", key = "#userId")
    @Transactional
    public UserDTO setActivityUserById(Integer userId, Boolean status){
        User user = userRepository.findById(userId).orElseThrow(()
                -> new UserException("User not found with id: " + userId));
        user.setActive(status);
        return userMapper.toDto(userRepository.save(user));
    }

    @CacheEvict(value = "users", key = "#userId")
    @Transactional
    public void deleteUserById(Integer userId){
        if(userRepository.findById(userId).isEmpty())
            throw new UserException("User not found with id: " + userId);
        userRepository.deleteById(userId);
    }
}
