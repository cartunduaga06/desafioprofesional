package com.dmh.backend.service;

import com.dmh.backend.dto.AccountResponse;
import com.dmh.backend.dto.AccountUpdateRequest;
import com.dmh.backend.model.User;
import com.dmh.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;

/**
 * Servicio para operaciones relacionadas con la cuenta (cvu, alias, saldo).
 */
@Service
@RequiredArgsConstructor
public class AccountService {

    private final UserRepository userRepository;

    /**
     * Obtiene los datos de la cuenta de un usuario.
     *
     * @param id identificador del usuario
     * @return información de la cuenta
     */
    public AccountResponse getAccount(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        return new AccountResponse(user.getId(), user.getCvu(), user.getAlias(), user.getBalance());
    }

    /**
     * Actualiza el alias de la cuenta si es diferente al actual y no está en uso.
     *
     * @param id      identificador del usuario
     * @param request datos de actualización
     * @return cuenta actualizada
     */
    @Transactional
    public AccountResponse updateAccount(Long id, AccountUpdateRequest request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("User not found"));
        if (request.getAlias() != null && !request.getAlias().isBlank() && !request.getAlias().equals(user.getAlias())) {
            // Validar unicidad de alias
            if (userRepository.existsByAlias(request.getAlias())) {
                throw new IllegalArgumentException("Alias is already in use");
            }
            user.setAlias(request.getAlias());
        }
        User saved = userRepository.save(user);
        return new AccountResponse(saved.getId(), saved.getCvu(), saved.getAlias(), saved.getBalance());
    }
}
