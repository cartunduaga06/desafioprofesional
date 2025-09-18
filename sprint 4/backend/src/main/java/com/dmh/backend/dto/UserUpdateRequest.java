package com.dmh.backend.dto;

import lombok.Data;

/**
 * Request para actualizar parcialmente los datos del usuario. Todos los campos son opcionales y sólo se
 * modificarán aquellos que no sean null o vacíos.
 */
@Data
public class UserUpdateRequest {
    private String firstName;
    private String lastName;
    private String dni;
    private String phoneNumber;
    private String email;
}