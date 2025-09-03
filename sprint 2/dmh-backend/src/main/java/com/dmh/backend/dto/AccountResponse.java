package com.dmh.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para devolver datos de la cuenta (CVU, alias, saldo).
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AccountResponse {
    private Long id;
    private String cvu;
    private String alias;
    private BigDecimal balance;
}