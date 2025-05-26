package org.example.entity;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private int dni;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Rol rol;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "phone_id")
    @EqualsAndHashCode.Exclude
    private Phone phone;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "address_id")
    @EqualsAndHashCode.Exclude
    private Address address;

    @OneToOne
    @JoinColumn(name = "user_id")
    @EqualsAndHashCode.Exclude
    private User user;

}