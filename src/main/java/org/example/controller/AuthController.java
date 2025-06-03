package org.example.controller;

import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.dto.PasswordResetRequestDTO;
import org.example.dto.UserDTO;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.example.dto.PasswordRestDTO;

import java.util.HashMap;
import java.util.Map;
@RestController
@RequestMapping("/api/login")
public class AuthController {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    @PostMapping("/forgot-password")
    public ResponseEntity<?> forgotPassword(@RequestBody PasswordResetRequestDTO request) {
        User user = userRepository.findByUsername(request.getEmail());

        if (user == null) {
            return ResponseEntity.badRequest().body("No existe un usuario con ese correo electrónico");
        }

        authService.createPasswordResetTokenForUser(user);

        Map<String, String> response = new HashMap<>();
        response.put("message", "Se ha enviado un correo electrónico para restablecer la contraseña");

        return ResponseEntity.ok(response);
    }

    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody PasswordRestDTO passwordReset) {
        String result = authService.validatePasswordResetToken(passwordReset.getToken());

        if (!result.equals("valid")) {
            return ResponseEntity.badRequest().body("Token inválido o expirado");
        }

        User user = authService.getUserByPasswordResetToken(passwordReset.getToken());
        authService.changeUserPassword(user, passwordReset.getNewPassword());

        Map<String, String> response = new HashMap<>();
        response.put("message", "Contraseña actualizada correctamente");

        return ResponseEntity.ok(response);
    }
    @GetMapping("/oauth2/code/google")
    public void loginSuccess(HttpServletResponse response) throws IOException {
        response.sendRedirect("http://localhost:5173/dashboard");
    }
}
