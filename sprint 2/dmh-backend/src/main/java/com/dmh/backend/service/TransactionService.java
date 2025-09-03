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
}