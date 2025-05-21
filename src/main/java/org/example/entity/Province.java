package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Province {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}