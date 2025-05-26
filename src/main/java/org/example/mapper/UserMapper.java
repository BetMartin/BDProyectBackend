package org.example.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.mapstruct.factory.Mappers;


import java.util.List;

@Mapper(componentModel = "spring", uses = {RolMapper.class})
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "id", source = "user_id")
    @Mapping(target = "userName", source = "username")
    @Mapping(target = "firstName", source = "person.firstName")
    @Mapping(target = "lastName", source = "person.lastName")
    @Mapping(target = "dni", source = "person.dni")
    @Mapping(target = "phone", source = "person.phone.number")
    @Mapping(target = "address", source = "person.address.street")
    @Mapping(target = "rol", source = "person.rol")
    UserDTO userToUserDTO(User user);

    @Mapping(target = "user_id", source = "id")
    @Mapping(target = "username", source = "userName")
    @Mapping(target = "person.firstName", source = "firstName")
    @Mapping(target = "person.lastName", source = "lastName")
    @Mapping(target = "person.dni", source = "dni")
    @Mapping(target = "person.phone.number", source = "phone")
    @Mapping(target = "person.address.street", source = "address")
    @Mapping(target = "person.rol", source = "rol")
    User userDTOToUser(UserDTO userDTO);

    List<UserDTO> toUserDTOList(List<User> users);
    List<User> toUserList(List<UserDTO> userDTOs);

}