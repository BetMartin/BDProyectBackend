package org.example.service.ServiceInterface;

import org.example.dto.UserDTO;
import java.util.List;

public interface UserService {
    List<UserDTO> findAll(); // Obtener todos los usuarios en formato DTO
    UserDTO findById(Long id); // Obtener un usuario específico por su ID
    UserDTO create(UserDTO userDTO); // Crear un nuevo usuario
    UserDTO update(Long id, UserDTO userDTO); // Actualizar un usuario existente
    void delete(Long id); //
}