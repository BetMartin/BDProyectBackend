package org.example.controller;

import lombok.RequiredArgsConstructor;
import org.example.dto.UserDTO;
import org.example.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam("userName") String username,
            @RequestParam String password
    ) {
        try {
            System.out.println("Recibida solicitud de login para usuario: " + username);
            UserDTO userDTO = userService.login(username, password);
            return ResponseEntity.ok(userDTO);
        } catch (RuntimeException e) {
            System.err.println("Error durante el login: " + e.getMessage());

            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Credenciales inválidas: " + e.getMessage());
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDTO userDTO) {
        try {
            UserDTO createdUser = userService.createUser(userDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al registrar el usuario: " + e.getMessage());
        }
    }
}







//    @GetMapping
//    public ResponseEntity<List<UserDTO>> getAllUsers() {
//        List<UserDTO> users = userService.findAll();
//        return ResponseEntity.ok(users);
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
//        return userService.findById(id)
//                .map(ResponseEntity::ok)
//                .orElse(ResponseEntity.notFound().build());
//    }


//    }
