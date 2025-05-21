package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Phone {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String number;
}