package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.dto.RolDTO;
import org.example.dto.UserDTO;
import org.example.entity.Rol;
import org.example.entity.User;
import org.example.Repository.UserRepository;
import org.example.service.ServiceInterface.UserService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserDTO authenticateUser(String username, String password) {
        return userRepository.findByUsername(username)
                .filter(user -> validatePassword(password, user.getPassword()))
                .map(this::toDTO)
                .orElse(null);
    }


    private boolean validatePassword(String inputPassword, String storedPassword) {
        String encryptedInputPassword = User.encriptarClave(inputPassword);
        return encryptedInputPassword.equals(storedPassword);
    }


    @Override
    public List<UserDTO> findAll() {
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO findById(Long id) {
        return userRepository.findById(id)
                .map(this::toDTO)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
    }

    @Override
    public UserDTO create(UserDTO userDTO) {
        User user = toEntity(userDTO);
        user.setUsername(userDTO.getUserName());
        user.setPassword(User.encriptarClave(user.getPassword()));
        User savedUser = userRepository.save(user);
        return toDTO(savedUser);
    }

    @Override
    public UserDTO update(Long id, UserDTO userDTO) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        
        User userToUpdate = toEntity(userDTO);
        userToUpdate.setUser_id(id);
        userToUpdate.setPassword(User.encriptarClave(userToUpdate.getPassword()));
        
        User updatedUser = userRepository.save(userToUpdate);
        return toDTO(updatedUser);
    }

    @Override
    public void delete(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("Usuario no encontrado con ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // Métodos auxiliares para conversiones
    private UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        UserDTO userDTO = new UserDTO();
        userDTO.setId(user.getUser_id());
        userDTO.setUserName(user.getUsername());
        userDTO.setPassword(user.getPassword());

        if (user.getPerson() != null) {
            userDTO.setRol(mapRolToRolDTO(user.getPerson().getRol()));
        }
        return userDTO;
    }

    private User toEntity(UserDTO userDTO) {
        if (userDTO == null) {
            return null;
        }

        User user = new User();
        user.setUser_id(userDTO.getId());
        user.setUsername(userDTO.getUserName());
        user.setPassword(userDTO.getPassword());
        return user;
    }

    private RolDTO mapRolToRolDTO(Rol rol) {
        if (rol == null) {
            return null;
        }
        return RolDTO.valueOf(rol.name());
    }
}