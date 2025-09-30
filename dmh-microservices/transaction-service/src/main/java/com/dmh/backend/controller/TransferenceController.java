package com.dmh.backend.controller;

import com.dmh.backend.dto.RecipientResponse;
import com.dmh.backend.dto.TransferRequest;
import com.dmh.backend.dto.TransferResponse;
import com.dmh.backend.service.TransferenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para operaciones de transferencia de dinero.
 */
@RestController
@RequestMapping("/accounts/{accountId}/transferences")
@Tag(name = "Transferencias", description = "Gestión de transferencias entre cuentas")
@RequiredArgsConstructor
public class TransferenceController {

    private final TransferenceService transferenceService;

    @Operation(summary = "Listar últimos destinatarios", description = "Devuelve los destinatarios de las últimas transferencias realizadas por la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de destinatarios",
                    content = @Content(schema = @Schema(implementation = RecipientResponse.class))),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<List<RecipientResponse>> getLastRecipients(@PathVariable Long accountId) {
        List<RecipientResponse> list = transferenceService.getLastRecipients(accountId);
        return ResponseEntity.ok(list);
    }

    @Operation(summary = "Realizar transferencia", description = "Realiza una transferencia a otra cuenta. Se requiere que el usuario autenticado sea titular de la cuenta de origen.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transferencia exitosa",
                    content = @Content(schema = @Schema(implementation = TransferResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "403", description = "Permisos insuficientes"),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada"),
            @ApiResponse(responseCode = "410", description = "Fondos insuficientes")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<TransferResponse> makeTransfer(
            @PathVariable Long accountId,
            @Valid @RequestBody TransferRequest request
    ) {
        TransferResponse response = transferenceService.transfer(accountId, request);
        return ResponseEntity.ok(response);
    }
}