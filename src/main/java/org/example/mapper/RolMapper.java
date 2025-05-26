package org.example.mapper;

import org.mapstruct.Mapper;
import org.example.dto.RolDTO;
import org.example.entity.Rol;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RolMapper {
    
    RolDTO toDto(Rol rol);
    
    Rol toEntity(RolDTO dto);
    
    List<RolDTO> toDtoList(List<Rol> roles);
    
    List<Rol> toEntityList(List<RolDTO> dtos);
}