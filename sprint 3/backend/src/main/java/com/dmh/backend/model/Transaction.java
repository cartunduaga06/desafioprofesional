package com.dmh.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa un movimiento de dinero (transacción) asociado a un usuario (cuenta).
 */
@Entity
@Table(name = "transactions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /**
     * Monto de la transacción. Puede ser positivo (depósito) o negativo (retiro).
     */
    @NotNull
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Tipo de la transacción: por ejemplo, "DEPOSIT", "WITHDRAWAL", "PAYMENT".
     */
    @NotBlank
    @Column(nullable = false, length = 50)
    private String type;

    /**
     * Marca temporal de la transacción.
     */
    @NotNull
    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime date;

    /**
     * Descripción opcional de la transacción.
     */
    @Column(length = 255)
    private String description;
}