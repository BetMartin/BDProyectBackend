package org.example.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    @EqualsAndHashCode.Exclude
    private Province province;

    //Metodo para obtener direccion como string
    public String getAddressStr() {
        return String.format("%s %s %s. %s",
                this.street,
                this.streetNumber,
                this.apartment,
                this.province.getName());
    }
}