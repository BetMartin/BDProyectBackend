package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.PasswordResetToken;
import org.example.entity.User;
import org.example.repository.PasswordResetTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository usuarioRepository;
    private final EmailService emailService;

    private static final int TOKEN_EXPIRATION_MINUTES = 30;



    public void sendResetEmail(String email) {
        log.info("Iniciando proceso de recuperación para: {}", email);

        try {
            // Buscar usuarios con el mismo email - usamos findAll en lugar de findByUsername
            List<User> usuarios = usuarioRepository.findAllByUsername(email);

            if (usuarios.isEmpty()) {
                log.warn("Usuario no encontrado: {}", email);
                return;
            }

            log.info("Se encontraron {} usuarios con email {}", usuarios.size(), email);

            // Usar el primer usuario encontrado
            User usuario = usuarios.get(0);
            // Eliminar tokens anteriores
            tokenRepository.deleteByUsuario_Username(email);
            log.info("Tokens anteriores eliminados");

            // Generar nuevo token
            String token = generateSecureToken();

            // Prevenir NullPointerException
            String firstName = "Usuario";
            if (usuario.getPerson() != null && usuario.getPerson().getFirstName() != null) {
                firstName = usuario.getPerson().getFirstName();
            }

            // Crear token
            PasswordResetToken resetToken = new PasswordResetToken(token, usuario, TOKEN_EXPIRATION_MINUTES);
            tokenRepository.save(resetToken);
            log.info("Token guardado en base de datos: {}", token);

            // Enviar email con try-catch interno para evitar que falle toda la transacción
            try {
                emailService.sendPasswordResetEmail(usuario.getUsername(), firstName, token);
                log.info("Email enviado correctamente a {}", email);
            } catch (Exception e) {
                log.error("Error al enviar email: {}", e.getMessage(), e);
            }
        } catch (Exception e) {
            log.error("Error en proceso de recuperación: {}", e.getMessage(), e);
        }
    }

        @Scheduled(cron = "0 0 */6 * * *") // Cada 6 horas
        public void purgeExpiredTokens() {
            tokenRepository.deleteByFechaExpiracionBefore(new Date());
            log.info("Limpieza de tokens expirados completada");
        }


    public void updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token inválido"));

        if (resetToken.isExpired()) {
            throw new IllegalArgumentException("El token ha expirado");
        }

        if (resetToken.getUsado()) {
            throw new IllegalArgumentException("El token ya fue utilizado");
        }

        // Encripta la nueva contraseña
        String encryptedPassword = encryptPassword(newPassword);

        // Actualiza la contraseña del usuario en la base de datos
        User usuario = resetToken.getUsuario();
        usuario.setPassword(encryptedPassword);
        usuarioRepository.save(usuario);

        // Marca el token como usado
        resetToken.setUsado(true);
        tokenRepository.save(resetToken);

        log.info("Contraseña actualizada para usuario: {}", usuario.getUsername());
    }

    public boolean validateToken(String token) {
        Optional<PasswordResetToken> resetToken = tokenRepository.findByToken(token);
        return resetToken.isPresent() &&
                !resetToken.get().isExpired() &&
                !resetToken.get().getUsado();
    }


    private String encryptPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();

            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }

            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
    private String generateSecureToken() {
        return UUID.randomUUID().toString();
    }
}