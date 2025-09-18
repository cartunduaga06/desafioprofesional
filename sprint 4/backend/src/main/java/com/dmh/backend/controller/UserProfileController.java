package com.dmh.backend.controller;

import com.dmh.backend.dto.UserResponse;
import com.dmh.backend.dto.UserUpdateRequest;
import com.dmh.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones del perfil de usuario.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Usuarios", description = "Operaciones sobre el perfil de usuario")
@RequiredArgsConstructor
public class UserProfileController {

    private final UserService userService;

    @Operation(summary = "Consultar perfil", description = "Obtiene los datos del usuario por ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario encontrado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado")
    })
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> getUser(@PathVariable Long userId) {
        UserResponse response = userService.getUserProfile(userId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar perfil", description = "Actualiza parcialmente los datos del usuario")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado"),
            @ApiResponse(responseCode = "404", description = "Usuario no encontrado"),
            @ApiResponse(responseCode = "409", description = "Datos en conflicto (email o DNI)")
    })
    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable Long userId,
                                                  @RequestBody UserUpdateRequest request) {
        UserResponse response = userService.updateUser(userId, request);
        return ResponseEntity.ok(response);
    }
}