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
    @JoinColumn(name = "user_id")  // Cambia el nombre de la columna para que coincida
    private User usuario;

    // Cambia el nombre del campo o agrega la anotación
    @Column(name = "expiry_date")
    private Date fechaExpiracion;

    private boolean usado = false;

    // Constructor, getters y setters
    public PasswordResetToken() {}

    public PasswordResetToken(String token, User usuario, int expirationMinutes) {
        this.token = token;
        this.usuario = usuario;

        // Asegúrate de que fechaExpiracion se establezca correctamente
        Calendar calendario = Calendar.getInstance();
        calendario.add(Calendar.MINUTE, expirationMinutes);
        this.fechaExpiracion = calendario.getTime();
    }

    public boolean isExpired() {
        return fechaExpiracion.before(new Date());
    }

    public boolean getUsado() {
        return usado;
    }
}