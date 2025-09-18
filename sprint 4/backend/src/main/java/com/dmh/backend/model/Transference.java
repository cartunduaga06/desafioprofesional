package com.dmh.backend.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una transferencia de dinero de un usuario a otro.
 */
@Entity
@Table(name = "transferences")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Transference {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "from_user_id", nullable = false)
    private User fromUser;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "to_user_id", nullable = false)
    private User toUser;

    /**
     * Monto transferido. Siempre positivo.
     */
    @Column(nullable = false)
    private BigDecimal amount;

    /**
     * Fecha y hora de la transferencia.
     */
    @Column(nullable = false)
    private LocalDateTime date;

    /**
     * Descripción opcional de la transferencia.
     */
    @Column(length = 255)
    private String description;
}