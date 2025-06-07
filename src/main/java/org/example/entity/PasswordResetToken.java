package org.example.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Entity
@Getter
@Setter
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String token;

    @ManyToOne
    private User usuario;

    private Date fechaExpiracion;

    private boolean usado;

    public PasswordResetToken() {}
    public PasswordResetToken(String token, User usuario, int expirationMinutes) {
        this.token = token;
        this.usuario = usuario;
        this.usado = false;

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MINUTE, expirationMinutes);
        this.fechaExpiracion = calendar.getTime();
    }

    public boolean isExpired() {
        return fechaExpiracion.before(new Date());
    }

    public boolean getUsado() {
        return usado;
    }
}