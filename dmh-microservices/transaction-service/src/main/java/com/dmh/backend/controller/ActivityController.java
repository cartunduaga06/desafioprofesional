package com.dmh.backend.controller;

import com.dmh.backend.dto.TransactionResponse;
import com.dmh.backend.dto.TransferenceRequest;
import com.dmh.backend.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Controlador que gestiona la actividad de la cuenta y el ingreso de dinero.
 */
@RestController
@RequestMapping("/accounts/{accountId}")
@Tag(name = "Actividad", description = "Consulta de actividades y registro de ingresos")
@RequiredArgsConstructor
public class ActivityController {

    private final TransactionService transactionService;

    @Operation(summary = "Listar actividad", description = "Obtiene el historial de movimientos de la cuenta en orden descendente. Permite filtros opcionales por monto, fechas o tipo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de actividades",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Cuenta no encontrada")
    })
    @GetMapping("/activity")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<List<TransactionResponse>> getActivity(
            @PathVariable Long accountId,
            @RequestParam(value = "minAmount", required = false) BigDecimal minAmount,
            @RequestParam(value = "maxAmount", required = false) BigDecimal maxAmount,
            @RequestParam(value = "startDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(value = "endDate", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(value = "type", required = false) String type
    ) {
        List<TransactionResponse> responses = transactionService.getActivity(accountId, minAmount, maxAmount, startDate, endDate, type);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Detalle de movimiento", description = "Obtiene el detalle de una transacción específica de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Transacción encontrada",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "404", description = "Transacción no encontrada")
    })
    @GetMapping("/activity/{transactionId}")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<TransactionResponse> getTransaction(@PathVariable Long accountId, @PathVariable Long transactionId) {
        TransactionResponse response = transactionService.getTransaction(accountId, transactionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar ingreso", description = "Registra un ingreso de dinero desde una tarjeta a la cuenta. El monto debe ser positivo.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Ingreso registrado",
                    content = @Content(schema = @Schema(implementation = TransactionResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Cuenta o tarjeta no encontrada"),
            @ApiResponse(responseCode = "409", description = "Conflicto de datos")
    })
    @PostMapping("/transferences")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<TransactionResponse> createIncome(
            @PathVariable Long accountId,
            @RequestBody @jakarta.validation.Valid TransferenceRequest request
    ) {
        TransactionResponse response = transactionService.createIncome(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}