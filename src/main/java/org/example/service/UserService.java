package org.example.service;

import lombok.RequiredArgsConstructor;
import org.example.dto.RolDTO;
import org.example.dto.UserDTO;
import org.example.entity.Person;
import org.example.entity.User;
import org.example.mapper.RolMapper;
import org.example.mapper.UserMapper;
import org.example.repository.PersonRepository;
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
    private final RolMapper rolMapper;
    private final PersonRepository personRepository;

    @Transactional(readOnly = true)
    public List<UserDTO> findAll() {
        return userMapper.toUserDTOList(userRepository.findAllWithDetails());
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findById(Long id) {
        return userRepository.findByIdWithDetails(id)
                .map(userMapper::userToUserDTO);
    }

    @Transactional(readOnly = true)
    public Optional<UserDTO> findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::userToUserDTO);
    }

    @Transactional
    public UserDTO login(String username, String password) {
        try {
            // Validar entrada
            if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
                throw new RuntimeException("El nombre de usuario y la contraseña son obligatorios");
            }

            // Buscar usuario
            User user = userRepository.findUserForLogin(username)
                    .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

            // Validar contraseña
            String passwordEncriptada = user.encriptarClave(password);
            if (!user.getPassword().equals(passwordEncriptada)) {
                throw new RuntimeException("Contraseña incorrecta");
            }


            // Crear DTO con todos los datos
            UserDTO responseDto = new UserDTO();
            responseDto.setId(user.getUser_id());
            responseDto.setUserName(user.getUsername());
            responseDto.setPassword(user.getPassword());

            // Obtener datos de la persona
            Person person = personRepository.findByUser(user.getUser_id());
            responseDto.setDni(person.getDni());
            responseDto.setFirstName(person.getFirstName());
            responseDto.setLastName(person.getLastName());

            // Obtener teléfono
            if (person.getPhone() != null) {
                responseDto.setPhone(person.getPhone().getNumber());
            }


            // Obtener dirección
            if (person.getAddress() != null) {
                responseDto.setAddress(person.getAddress().getAddressStr());
            }

            // Obtener rol
            if (person.getRol() != null) {
                responseDto.setRol(rolMapper.toDto(person.getRol()));
            }

            return responseDto;
        } catch (Exception e) {
            throw new RuntimeException("Error durante el login: " + e.getMessage());
        }
    }
}



//    public UserDTO mapToUserDTO(Person person) {
//        UserDTO userDTO = new UserDTO();
//
//        // Datos del usuario
//        if (person.getUser() != null) {
//            userDTO.setId(person.getUser().getUser_id());
//            userDTO.setUserName(person.getUser().getUsername());
//            userDTO.setPassword(person.getUser().getPassword());
//        }
//
//        // Datos de la persona
//        userDTO.setDni(person.getDni());
//        userDTO.setFirstName(person.getFirstName());
//        userDTO.setLastName(person.getLastName());
//
//        // Teléfono
//        if (person.getPhone() != null) {
//            userDTO.setPhone(Integer.parseInt(person.getPhone().getNumber()));
//        }
//
//        // Dirección usando el método getAddressStr()
//        if (person.getAddress() != null) {
//            userDTO.setAddress(person.getAddress().getAddressStr());
//        }
//
//        // Rol
//        if (person.getRol() != null) {
//            userDTO.setRol(rolMapper.toDto(person.getRol()));
//        }
//
//        return userDTO;
//    }
//
//    @Transactional(readOnly = true)
//    public UserDTO getUserByUserId(Long userId) {
//        Person person = personRepository.findByUser(userId);
//        if (person == null) {
//            throw new RuntimeException("No se encontró el usuario con ID: " + userId);
//        }
//        return mapToUserDTO(person);
//    }


//    @Transactional(readOnly = true)
//    public UserDTO createUser(UserDTO userDTO) {
//
//        // Validar datos obligatorios
//        if (userDTO.getUserName() == null || userDTO.getUserName().trim().isEmpty() ||
//                userDTO.getPassword() == null || userDTO.getPassword().trim().isEmpty()) {
//            throw new IllegalArgumentException("El nombre de usuario y la contraseña son obligatorios");
//        }
//        // Crear y configurar el usuario
//        User user = new User();
//        user.setUsername(userDTO.getUserName());
//        user.setPassword(encriptarClave(userDTO.getPassword()));
//
//        user = userRepository.save(user);
//
//        //Crear y configurar la persona
//        Person person = new Person();
//        person.setDni(userDTO.getDni());
//        person.setFirstName(userDTO.getFirstName());
//        person.setLastName(userDTO.getLastName());
//        if (userDTO.getRol() != null) {
//            person.setRol(rolMapper.toEntity(userDTO.getRol()));
//        }
//        person.setUser(user);
//
//        // Guardar la persona primero
//        Person personSaved = personRepository.save(person);
//
//
//        return userMapper.toDto(user);
//    }
//private String encriptarClave(String password) {
//        try {
//            MessageDigest digest = MessageDigest.getInstance("SHA-256");
//            byte[] hash = digest.digest(password.getBytes());
//            StringBuilder hexString = new StringBuilder();
//
//            for (byte b : hash) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1) hexString.append('0');
//                hexString.append(hex);
//            }
//
//            return hexString.toString();
//        } catch (NoSuchAlgorithmException e) {
//            throw new RuntimeException("Error al encriptar la contraseña", e);
//        }
//    }

