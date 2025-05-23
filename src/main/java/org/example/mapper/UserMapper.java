package org.example.mapper;

import org.mapstruct.InheritInverseConfiguration;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.mapstruct.Mappings;


import java.util.List;

@Mapper(componentModel = "spring", uses = {RolMapper.class})
public interface UserMapper {

    @Mappings({
            @Mapping(source = "user.user_id", target = "id"),                    // Mapeo de id
            @Mapping(source = "user.username", target = "userName"),            // Mapeo de username
            @Mapping(source = "user.password", target = "password"),            // Mapeo de password
            @Mapping(source = "user.person.dni", target = "dni"),               // DNI desde Person
            @Mapping(source = "user.person.firstName", target = "firstName"),   // Nombre desde Person
            @Mapping(source = "user.person.lastName", target = "lastName"),     // Apellido desde Person
            @Mapping(source = "user.person.rol", target = "rol"),               // Rol usando RolMapper
            @Mapping(target = "phone", expression = "java(getPhoneNumber(user))"),  // Teléfono personalizado
            @Mapping(target = "address", expression = "java(getAddressStr(user))") // Dirección personalizada
    })
    UserDTO toDto(User user);

    @InheritInverseConfiguration
    @Mappings({
            @Mapping(target = "person", ignore = true)})
    User toEntity(UserDTO dto);

    List<UserDTO> toDtoList(List<User> users);
    
    List<User> toEntityList(List<UserDTO> dtos);

    // Metodo personalizado para obtener el número de teléfono
    default int getPhoneNumber(User user) {
        if (user.getPerson() != null && user.getPerson().getPhone() != null) {
            return Integer.parseInt(user.getPerson().getPhone().getNumber());
        }
        return 0; // Valor predeterminado en caso de que no haya teléfono
    }

    // Metodo personalizado para obtener la dirección
    default String getAddressStr(User user) {
        if (user.getPerson() != null && user.getPerson().getAddress() != null) {
            return user.getPerson().getAddress().getAddressStr();
        }
        return ""; // Valor predeterminado en caso de que no haya dirección
    }


}