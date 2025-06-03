package org.example.service;

import org.example.entity.User;
import org.example.entity.PasswordResetToken;
import org.example.repository.PasswordResetTokenRepository;
import org.example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private EmailService emailService;

    @Transactional
    public void createPasswordResetTokenForUser(User user) {
        String token = UUID.randomUUID().toString();

        // Eliminar tokens anteriores si existen
        tokenRepository.findAll().stream()
                .filter(t -> t.getUser().getUser_id().equals(user.getUser_id()))
                .forEach(tokenRepository::delete);

        // Crear nuevo token
        PasswordResetToken myToken = new PasswordResetToken();
        myToken.setToken(token);
        myToken.setUser(user);

        // Establecer fecha de expiración a 24 horas desde ahora
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.HOUR, 24);
        myToken.setExpiryDate(calendar.getTime());

        tokenRepository.save(myToken);

        // Enviar correo
        emailService.sendPasswordResetEmail(user.getUsername(), token);
    }

    @Transactional
    public String validatePasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);

        if (passToken == null) {
            return "invalidToken";
        }

        // Verificar si el token ha expirado
        if (new Date().after(passToken.getExpiryDate())) {
            tokenRepository.delete(passToken);
            return "expired";
        }

        return "valid";
    }

    @Transactional
    public void changeUserPassword(User user, String newPassword) {
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

    public User getUserByPasswordResetToken(String token) {
        PasswordResetToken passToken = tokenRepository.findByToken(token);
        return passToken != null ? passToken.getUser() : null;
    }
}
