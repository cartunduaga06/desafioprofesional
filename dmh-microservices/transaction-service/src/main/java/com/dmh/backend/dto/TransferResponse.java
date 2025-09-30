package com.dmh.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Respuesta devuelta tras realizar una transferencia.
 */
@Data
@AllArgsConstructor
public class TransferResponse {
    private Long id;
    private Long fromAccountId;
    private Long toAccountId;
    private BigDecimal amount;
    private LocalDateTime date;
    private String description;
}