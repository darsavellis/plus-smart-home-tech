package ru.yandex.practicum.delivery.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@Table(name = "address")
public class Address {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID addressId;

    @NotEmpty
    private String country;

    @NotEmpty
    private String city;

    @NotEmpty
    private String street;

    @NotEmpty
    private String house;
    private String flat;
}
