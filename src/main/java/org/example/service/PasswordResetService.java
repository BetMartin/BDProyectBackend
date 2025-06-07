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
            Optional<User> usuarioOpt = usuarioRepository.findByUsername(email);

            if (usuarioOpt.isEmpty()) {
                log.warn("Intento de recuperación para email no existente: {}", email);
                return;
            }

            User usuario = usuarioOpt.get();

            // Elimina tokens anteriores del usuario
            tokenRepository.deleteByUsuario_Username(email);

            // Genera un token único
            String token = generateSecureToken();

            // Crea un nuevo token
            PasswordResetToken resetToken = new PasswordResetToken(token, usuario, TOKEN_EXPIRATION_MINUTES);
            tokenRepository.save(resetToken);

            // Envía el email
            emailService.sendPasswordResetEmail(usuario.getUsername(), usuario.getPerson().getFirstName(), token);

            log.info("Token de recuperación creado para usuario: {}", email);
        }

        // Resto del código se mantiene igual

        private String generateSecureToken() {
            return UUID.randomUUID().toString();
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
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] messageDigest = md.digest(password.getBytes());
            BigInteger number = new BigInteger(1, messageDigest);
            String hashtext = number.toString(16);
            while (hashtext.length() < 32) {
                hashtext = "0" + hashtext;
            }
            return hashtext;
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error al encriptar contraseña", e);
        }
    }
}