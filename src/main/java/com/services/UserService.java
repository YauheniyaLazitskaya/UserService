package com.services;

import com.entities.*;
import com.exceptions.UserException;
import com.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;


    public UserService(UserRepository userRepository) { this.userRepository = userRepository; }

    @Transactional
    public User createUser(User user){
        if (userRepository.findByEmail(user.getEmail()).isPresent())
            throw new UserException("User with this email (" + user.getEmail() + ") already exists.");
        return userRepository.save(user);
    }

    @Transactional
    public User updateUserByID(User newUser){
        if(userRepository.findById(newUser.getId()).isEmpty())
            throw new UserException("User with this id doesn't exists.");
        return userRepository.save(newUser);
    }

    public User getUserById(Integer id){
        return userRepository.findById(id).orElseThrow(()
                -> new UserException("User not found with id: " + id));
    }

    public Page<User> getAllUsers(int page, int size){
        return userRepository.findAll(PageRequest.of(page, size,
                Sort.by("name").and(Sort.by("surname"))));
    }

    public User setActivityUserByID(Integer id, Boolean status){
        User user = userRepository.findById(id).orElseThrow(()
                -> new UserException("User not found with id: " + id));
        user.setActive(status);
        return userRepository.save(user);
    }

    public void deleteUserByID(Integer id){
        if(userRepository.findById(id).isEmpty())
            throw new UserException("User with this id (" + id + ") doesn't exists.");
        userRepository.deleteById(id);
    }
}
