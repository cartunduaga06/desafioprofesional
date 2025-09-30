package com.dmh.backend.service;

import com.dmh.backend.dto.RecipientResponse;
import com.dmh.backend.dto.TransferRequest;
import com.dmh.backend.dto.TransferResponse;
import com.dmh.backend.exception.InsufficientFundsException;
import com.dmh.backend.model.Transference;
import com.dmh.backend.model.Transaction;
import com.dmh.backend.model.User;
import com.dmh.backend.repository.TransferenceRepository;
import com.dmh.backend.repository.TransactionRepository;
import com.dmh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio que encapsula la lógica de negocio relacionada con transferencias entre cuentas.
 */
@Service
@RequiredArgsConstructor
public class TransferenceService {

    private final TransferenceRepository transferenceRepository;
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;

    /**
     * Realiza una transferencia de una cuenta a otra.
     *
     * @param fromUserId identificador del usuario origen (cuenta emisora)
     * @param request    datos de la transferencia
     * @return respuesta con datos de la transferencia registrada
     */
    @Transactional
    public TransferResponse transfer(Long fromUserId, TransferRequest request) {
        // Obtener usuarios origen y destino
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
        User toUser = userRepository.findById(request.getToAccountId())
                .orElseThrow(() -> new NoSuchElementException("Destination account not found"));
        // Validar que no se transfiera a sí mismo
        if (fromUser.getId().equals(toUser.getId())) {
            throw new IllegalArgumentException("Cannot transfer to the same account");
        }
        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        // Validar fondos suficientes
        if (fromUser.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds");
        }
        // Actualizar saldos
        fromUser.setBalance(fromUser.getBalance().subtract(amount));
        toUser.setBalance(toUser.getBalance().add(amount));
        userRepository.save(fromUser);
        userRepository.save(toUser);
        // Crear registro de transferencia
        Transference transference = new Transference();
        transference.setFromUser(fromUser);
        transference.setToUser(toUser);
        transference.setAmount(amount);
        LocalDateTime now = LocalDateTime.now();
        transference.setDate(now);
        String description = request.getDescription();
        if (description == null || description.isBlank()) {
            description = "Transferencia a " + toUser.getAlias();
        }
        transference.setDescription(description);
        Transference savedTransference = transferenceRepository.save(transference);
        // Registrar transacciones individuales (egreso e ingreso)
        Transaction outTx = new Transaction();
        outTx.setUser(fromUser);
        outTx.setAmount(amount.negate());
        outTx.setType("TRANSFER_OUT");
        outTx.setDate(now);
        outTx.setDescription("Transfer to " + toUser.getAlias());
        transactionRepository.save(outTx);
        Transaction inTx = new Transaction();
        inTx.setUser(toUser);
        inTx.setAmount(amount);
        inTx.setType("TRANSFER_IN");
        inTx.setDate(now);
        inTx.setDescription("Transfer from " + fromUser.getAlias());
        transactionRepository.save(inTx);
        return new TransferResponse(savedTransference.getId(), fromUser.getId(), toUser.getId(), amount, now, description);
    }

    /**
     * Obtiene los destinatarios de las últimas transferencias realizadas por un usuario.
     *
     * @param fromUserId identificador del usuario emisor
     * @return lista de destinatarios
     */
    public List<RecipientResponse> getLastRecipients(Long fromUserId) {
        User fromUser = userRepository.findById(fromUserId)
                .orElseThrow(() -> new NoSuchElementException("Account not found"));
        List<Transference> transfers = transferenceRepository.findTop5ByFromUserOrderByDateDesc(fromUser);
        // Utilizar LinkedHashSet para eliminar duplicados manteniendo el orden de aparición
        LinkedHashSet<User> recipients = new LinkedHashSet<>();
        for (Transference t : transfers) {
            recipients.add(t.getToUser());
        }
        return recipients.stream()
                .map(u -> new RecipientResponse(u.getId(), u.getFirstName(), u.getLastName(), u.getAlias(), u.getCvu()))
                .collect(Collectors.toList());
    }
}