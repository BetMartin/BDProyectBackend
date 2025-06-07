package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.PasswordResetService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/password")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
@Slf4j
public class PasswordController {

    private final PasswordResetService passwordResetService;

    @PostMapping("/reset-request")
    public ResponseEntity<?> forgotPassword(@RequestBody Map<String, String> request) {
        try {
            String email = request.get("email");
            if (email == null || email.isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "El email es requerido"));
            }

            passwordResetService.sendResetEmail(email);

            return ResponseEntity.ok()
                    .body(Map.of("message", "Si el email existe, recibirás un enlace de recuperación"));

        } catch (Exception e) {
            log.error("Error en reset-request: {}", e.getMessage());
            return ResponseEntity.ok()
                    .body(Map.of("message", "Si el email existe, recibirás un enlace de recuperación"));
        }
    }

    @PostMapping("/update")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        try {
            String token = request.get("token");
            String newPassword = request.get("newPassword");

            if (token == null || newPassword == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Token y nueva contraseña son requeridos"));
            }

            if (newPassword.length() < 6) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "La contraseña debe tener al menos 6 caracteres"));
            }

            passwordResetService.updatePassword(token, newPassword);

            return ResponseEntity.ok()
                    .body(Map.of("message", "Contraseña actualizada exitosamente"));

        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("message", e.getMessage()));
        } catch (Exception e) {
            log.error("Error en update password: {}", e.getMessage());
            return ResponseEntity.internalServerError()
                    .body(Map.of("message", "Error interno del servidor"));
        }
    }

    @GetMapping("/verify-token/{token}")
    public ResponseEntity<?> verifyResetToken(@PathVariable String token) {
        try {
            boolean isValid = passwordResetService.validateToken(token);
            return ResponseEntity.ok()
                    .body(Map.of("valid", isValid));
        } catch (Exception e) {
            return ResponseEntity.ok()
                    .body(Map.of("valid", false));
        }
    }
}