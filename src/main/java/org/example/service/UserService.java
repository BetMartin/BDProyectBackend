package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.entity.User;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userMapper.toDtoList(userRepository.findAll());
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto);
    }

    @Transactional(readOnly = true)
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public UserDTO login(String userName, String password) {
        // Validar entrada
        if (userName == null || userName.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario y la contraseña son obligatorios");
        }

        // Buscar usuario por nombre de usuario
        User user = userRepository.findByUsername(userName)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // Verificar contraseña usando el método de encriptación SHA-1
        String passwordEncriptada = encriptarClave(password);
        if (!user.getPassword().equals(passwordEncriptada)) {
            throw new RuntimeException("Contraseña incorrecta");
        }

        // Convertir a DTO y retornar
        return userMapper.toDto(user);
    }

    //Método para encriptar la clave
    private String encriptarClave(String password) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-1");
            byte[] hash = messageDigest.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar la clave", e);
        }
    }

}