package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long address_id;

    private String apartment;
    private String street;
    private Integer streetNumber;

    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;
}