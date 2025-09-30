package com.dmh.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Generic API response wrapper for simple messages.
 */
@Data
@AllArgsConstructor
public class GenericResponse {
    private String message;
}
