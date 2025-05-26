package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Data
@Entity
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long user_id;
    private String username;
    private String password;

    @OneToOne(mappedBy = "user", fetch = FetchType.EAGER)
    @EqualsAndHashCode.Exclude
    private Person person;

    public String encriptarClave(String password) {
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
            throw new RuntimeException("Error al encriptar la contraseña", e);
        }
    }

    //Metodo para devolver el rol del usuario
    public Rol getRol() {
        return person.getRol();
    }
}