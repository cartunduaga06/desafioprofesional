package com.dmh.backend.dto;

import lombok.Data;

/**
 * Request para actualizar datos de la cuenta (por ejemplo, alias).
 */
@Data
public class AccountUpdateRequest {
    private String alias;
}