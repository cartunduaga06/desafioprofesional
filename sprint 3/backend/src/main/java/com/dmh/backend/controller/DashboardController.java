package com.dmh.backend.controller;

import com.dmh.backend.dto.AccountResponse;
import com.dmh.backend.dto.TransactionResponse;
import com.dmh.backend.service.TransactionService;
import com.dmh.backend.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que expone los endpoints del dashboard relacionados con la cuenta y los movimientos.
 */
@RestController
@RequestMapping("/accounts")
@Tag(name = "Dashboard", description = "Operaciones para consultar saldo y movimientos")
@RequiredArgsConstructor
public class DashboardController {

    private final UserService userService;
    private final TransactionService transactionService;

    @Operation(summary = "Consultar datos de la cuenta", description = "Devuelve el CVU, alias y saldo disponible de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta encontrada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountResponse> getAccount(@PathVariable Long accountId) {
        AccountResponse response = userService.getAccount(accountId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener últimos movimientos", description = "Devuelve las últimas cinco transacciones de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Movimientos recuperados"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/{accountId}/transactions")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<List<TransactionResponse>> getLastTransactions(@PathVariable Long accountId) {
        List<TransactionResponse> list = transactionService.getLastTransactions(accountId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Actualizar datos de la cuenta", description = "Permite actualizar el alias de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cuenta actualizada"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Alias en uso")
    })
    @PatchMapping("/{accountId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<AccountResponse> updateAccount(@PathVariable Long accountId,
                                                        @RequestBody com.dmh.backend.dto.AccountUpdateRequest request) {
        AccountResponse response = userService.updateAccount(accountId, request);
        return ResponseEntity.ok(response);
    }
}