package com.dmh.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * DTO para devolver información de usuario sin exponer la contraseña.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String dni;
    private String phoneNumber;
    private String email;
    private String cvu;
    private String alias;
    private BigDecimal balance;
}