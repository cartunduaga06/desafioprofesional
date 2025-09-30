package com.dmh.backend.controller;

import com.dmh.backend.dto.CardResponse;
import com.dmh.backend.dto.CreateCardRequest;
import com.dmh.backend.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador para la gestión de tarjetas.
 */
@RestController
@RequestMapping("/accounts/{accountId}/cards")
@Tag(name = "Tarjetas", description = "CRUD de tarjetas asociadas a una cuenta")
@RequiredArgsConstructor
public class CardController {

    private final CardService cardService;

    @Operation(summary = "Listar tarjetas", description = "Obtiene todas las tarjetas asociadas a la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Lista de tarjetas",
                    content = @Content(schema = @Schema(implementation = CardResponse.class)))
    })
    @GetMapping
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<List<CardResponse>> listCards(@PathVariable Long accountId) {
        List<CardResponse> responses = cardService.listCards(accountId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Obtener tarjeta", description = "Obtiene una tarjeta específica de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarjeta encontrada",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    @GetMapping("/{cardId}")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<CardResponse> getCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        CardResponse response = cardService.getCard(accountId, cardId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Crear tarjeta", description = "Asocia una nueva tarjeta a la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Tarjeta creada",
                    content = @Content(schema = @Schema(implementation = CardResponse.class))),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "Número de tarjeta en uso")
    })
    @PostMapping
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<CardResponse> createCard(@PathVariable Long accountId,
                                                   @RequestBody @jakarta.validation.Valid CreateCardRequest request) {
        CardResponse response = cardService.addCard(accountId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Eliminar tarjeta", description = "Elimina una tarjeta de la cuenta")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Tarjeta eliminada"),
            @ApiResponse(responseCode = "404", description = "Tarjeta no encontrada")
    })
    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('USER') and #accountId == principal.id")
    public ResponseEntity<Void> deleteCard(@PathVariable Long accountId, @PathVariable Long cardId) {
        cardService.deleteCard(accountId, cardId);
        return ResponseEntity.ok().build();
    }
}