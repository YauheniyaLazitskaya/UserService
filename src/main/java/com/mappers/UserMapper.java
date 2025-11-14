package com.mappers;

import com.dto.usersDto.*;
import com.entities.User;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring", uses = {PaymentCardMapper.class})
public interface UserMapper {
    UserDTO toDto(User user);
    User toEntity(UserDTO userDTO);
    User toEntity(CreateUserDTO createUserDTO);
    List<UserDTO> toDtoList(List<User> users);
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDto(UpdateUserDTO newUserDTO, @MappingTarget User oldUser);
}
