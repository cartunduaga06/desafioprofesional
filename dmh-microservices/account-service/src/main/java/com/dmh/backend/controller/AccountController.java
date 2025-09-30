package com.dmh.backend.controller;

import com.dmh.backend.dto.AccountResponse;
import com.dmh.backend.dto.AccountUpdateRequest;
import com.dmh.backend.service.AccountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para operaciones sobre la cuenta (cvu, alias, saldo).
 */
@RestController
@RequestMapping("/accounts")
@Tag(name = "Cuentas", description = "Operaciones sobre la cuenta")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @Operation(summary = "Consultar cuenta", description = "Devuelve el CVU, alias y saldo disponible de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        AccountResponse response = accountService.getAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Actualizar cuenta", description = "Actualiza parcialmente los datos de la cuenta, actualmente sólo permite modificar el alias")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Alias en uso")
    })
    @PatchMapping("/{accountId}")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long accountId,
                                                         @RequestBody AccountUpdateRequest request) {
        AccountResponse response = accountService.updateAccount(accountId, request);
        return ResponseEntity.ok(response);
    }
}
