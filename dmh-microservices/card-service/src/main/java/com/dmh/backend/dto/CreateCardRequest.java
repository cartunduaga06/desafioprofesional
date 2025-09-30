package com.dmh.backend.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Request para asociar una nueva tarjeta a una cuenta.
 */
@Data
public class CreateCardRequest {
    @NotBlank(message = "Card number is required")
    @Size(min = 13, max = 19, message = "Card number must have between 13 and 19 digits")
    private String cardNumber;

    @NotBlank(message = "Holder name is required")
    private String holderName;

    @NotBlank(message = "Expiration date is required")
    private String expirationDate;
}