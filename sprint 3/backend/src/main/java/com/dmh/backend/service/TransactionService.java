package com.dmh.backend.service;

import com.dmh.backend.dto.TransactionResponse;
import com.dmh.backend.model.Transaction;
import com.dmh.backend.model.User;
import com.dmh.backend.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Servicio para operaciones relacionadas con transacciones.
 */
@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final com.dmh.backend.repository.UserRepository userRepository;
    private final com.dmh.backend.service.CardService cardService;

    /**
     * Obtiene las últimas cinco transacciones de un usuario dado su identificador.
     *
     * @param userId identificador del usuario
     * @return lista de transacciones convertidas a DTO
     */
    public List<TransactionResponse> getLastTransactions(Long userId) {
        User user = userService.getUserById(userId);
        List<Transaction> transactions = transactionRepository.findTop5ByUserOrderByDateDesc(user);
        return transactions.stream()
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getDate(),
                        tx.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene el historial de actividad de un usuario aplicando filtros opcionales. Las transacciones se devuelven
     * en orden descendente por fecha (más reciente primero).
     *
     * @param userId    identificador del usuario
     * @param minAmount monto mínimo (inclusive) para filtrar, puede ser null
     * @param maxAmount monto máximo (inclusive) para filtrar, puede ser null
     * @param startDate fecha de inicio (inclusive) para filtrar, puede ser null
     * @param endDate   fecha de fin (inclusive) para filtrar, puede ser null
     * @param type      tipo de transacción (ingreso/egreso), puede ser null
     * @return lista de transacciones convertidas a DTO
     */
    public List<TransactionResponse> getActivity(Long userId,
                                                 java.math.BigDecimal minAmount,
                                                 java.math.BigDecimal maxAmount,
                                                 java.time.LocalDate startDate,
                                                 java.time.LocalDate endDate,
                                                 String type) {
        User user = userService.getUserById(userId);
        List<Transaction> transactions = transactionRepository.findByUserOrderByDateDesc(user);
        return transactions.stream()
                .filter(tx -> {
                    // Filtrar por tipo si se proporciona
                    if (type != null && !type.isBlank()) {
                        return tx.getType().equalsIgnoreCase(type);
                    }
                    return true;
                })
                .filter(tx -> {
                    // Filtrar por fecha de inicio
                    if (startDate != null) {
                        return !tx.getDate().toLocalDate().isBefore(startDate);
                    }
                    return true;
                })
                .filter(tx -> {
                    // Filtrar por fecha de fin
                    if (endDate != null) {
                        return !tx.getDate().toLocalDate().isAfter(endDate);
                    }
                    return true;
                })
                .filter(tx -> {
                    // Filtrar por monto mínimo
                    if (minAmount != null) {
                        // Usar valor absoluto para abarcar egresos negativos
                        return tx.getAmount().abs().compareTo(minAmount) >= 0;
                    }
                    return true;
                })
                .filter(tx -> {
                    // Filtrar por monto máximo
                    if (maxAmount != null) {
                        return tx.getAmount().abs().compareTo(maxAmount) <= 0;
                    }
                    return true;
                })
                .map(tx -> new TransactionResponse(
                        tx.getId(),
                        tx.getAmount(),
                        tx.getType(),
                        tx.getDate(),
                        tx.getDescription()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una transacción específica de un usuario.
     *
     * @param userId         identificador del usuario
     * @param transactionId  identificador de la transacción
     * @return DTO con los datos de la transacción
     */
    public TransactionResponse getTransaction(Long userId, Long transactionId) {
        User user = userService.getUserById(userId);
        Transaction tx = transactionRepository.findByIdAndUser(transactionId, user)
                .orElseThrow(() -> new java.util.NoSuchElementException("Transaction not found"));
        return new TransactionResponse(tx.getId(), tx.getAmount(), tx.getType(), tx.getDate(), tx.getDescription());
    }

    /**
     * Registra un ingreso de dinero desde una tarjeta a la cuenta del usuario.
     * Valida que la tarjeta exista y pertenezca al usuario, que el monto sea positivo y actualiza el saldo.
     *
     * @param userId identificador del usuario
     * @param request datos de la transferencia
     * @return DTO con los datos de la transacción creada
     */
    @org.springframework.transaction.annotation.Transactional
    public TransactionResponse createIncome(Long userId, com.dmh.backend.dto.TransferenceRequest request) {
        // Validar existencia de la tarjeta y propiedad
        // El CardService lanzará NoSuchElementException si la tarjeta no existe o no pertenece al usuario
        com.dmh.backend.dto.CardResponse card = cardService.getCard(userId, request.getCardId());
        // Obtener usuario
        User user = userService.getUserById(userId);
        java.math.BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(java.math.BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        // Actualizar saldo del usuario
        java.math.BigDecimal newBalance = user.getBalance().add(amount);
        user.setBalance(newBalance);
        // Guardar actualización
        // Persistir el nuevo saldo en base de datos
        userRepository.save(user);
        // Crear transacción
        Transaction tx = new Transaction();
        tx.setUser(user);
        tx.setAmount(amount);
        tx.setType("INCOME");
        tx.setDate(java.time.LocalDateTime.now());
        // Descripción opcional, incluir información de la tarjeta enmascarada
        String desc = request.getDescription();
        if (desc == null || desc.isBlank()) {
            // Enmascarar número de tarjeta: mostrar últimos 4 dígitos
            String cardNumber = card.getCardNumber();
            String masked = cardNumber.substring(Math.max(0, cardNumber.length() - 4));
            desc = "Ingreso desde tarjeta ****" + masked;
        }
        tx.setDescription(desc);
        Transaction saved = transactionRepository.save(tx);
        return new TransactionResponse(saved.getId(), saved.getAmount(), saved.getType(), saved.getDate(), saved.getDescription());
    }

}