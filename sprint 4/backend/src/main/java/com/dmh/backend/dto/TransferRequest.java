package com.dmh.backend.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

/**
 * Request para realizar una transferencia de dinero a otra cuenta.
 */
@Data
public class TransferRequest {
    @NotNull(message = "Destination account ID is required")
    private Long toAccountId;

    @NotNull(message = "Amount is required")
    @DecimalMin(value = "0.0", inclusive = false, message = "Amount must be positive")
    private BigDecimal amount;

    private String description;
}