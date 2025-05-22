package org.example.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.UserDTO;
import org.example.entity.User;


import java.util.List;

@Mapper(componentModel = "spring", uses = {RolMapper.class})
public interface UserMapper {

    @Mapping(source = "user.username", target = "userName")
    @Mapping(source = "user.password", target = "password")
    @Mapping(source = "user.person.dni", target = "dni")
    @Mapping(source = "user.person.firstname", target = "firstName")
    @Mapping(source = "user.person.lastname", target = "lastName")
    @Mapping(source = "user.person.phone.number", target = "phone")
    @Mapping(source = "user.person.address", target = "address", qualifiedByName = "formatAddress")
    @Mapping(source = "user.person.rol", target = "rol")
    @Mapping(target = "addressStr", expression = "java(user.getPerson().getAddress().getAddressStr())")
    UserDTO toDto(User user);


    @Mapping(target = "username", source = "userName")
    @Mapping(target = "person.dni", source = "dni")
    @Mapping(target = "person.firstname", source = "firstName")
    @Mapping(target = "person.lastname", source = "lastName")
    @Mapping(target = "person.phone.number", source = "phone")
    @Mapping(target = "person.rol", source = "rol")
    User toEntity(UserDTO dto);

    List<UserDTO> toDtoList(List<User> users);
    
    List<User> toEntityList(List<UserDTO> dtos);
}