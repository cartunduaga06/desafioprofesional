package com.dmh.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Response payload for successful login.
 */
@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String firstName;
    private String lastName;
    private String email;
    private String cvu;
    private String alias;
}
