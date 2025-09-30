package com.dmh.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Representa una tarjeta de crédito o débito asociada a un usuario (cuenta).
 */
@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Número de tarjeta. En un entorno real debería almacenarse de forma segura/enmascarada.
     */
    @NotBlank
    @Size(min = 13, max = 19)
    @Column(name = "card_number", nullable = false, length = 20)
    private String cardNumber;

    /**
     * Nombre del titular que aparece en la tarjeta.
     */
    @NotBlank
    @Column(name = "holder_name", nullable = false, length = 100)
    private String holderName;

    /**
     * Fecha de expiración en formato MM/YY o MM/YYYY.
     */
    @NotBlank
    @Column(name = "expiration_date", nullable = false, length = 7)
    private String expirationDate;
}