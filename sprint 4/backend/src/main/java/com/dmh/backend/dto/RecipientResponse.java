package com.dmh.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Representa un destinatario de transferencia.
 */
@Data
@AllArgsConstructor
public class RecipientResponse {
    private Long id;
    private String firstName;
    private String lastName;
    private String alias;
    private String cvu;
}