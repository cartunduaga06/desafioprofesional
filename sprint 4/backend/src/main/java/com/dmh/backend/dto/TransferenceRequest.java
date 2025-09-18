package com.dmh.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request para registrar un ingreso de dinero desde una tarjeta.
 */
@Data
public class TransferenceRequest {
    @NotNull(message = "Card ID is required")
    private Long cardId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    private String description;
}