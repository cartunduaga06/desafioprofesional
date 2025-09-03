package com.dmh.backend.service;

import com.dmh.backend.dto.CardResponse;
import com.dmh.backend.dto.CreateCardRequest;
import com.dmh.backend.model.Card;
import com.dmh.backend.model.User;
import com.dmh.backend.repository.CardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

/**
 * Servicio para la gestión de tarjetas asociadas a un usuario.
 */
@Service
@RequiredArgsConstructor
public class CardService {

    private final CardRepository cardRepository;
    private final UserService userService;

    /**
     * Devuelve todas las tarjetas asociadas al usuario.
     *
     * @param userId identificador del usuario
     * @return lista de tarjetas convertidas a DTO
     */
    public List<CardResponse> listCards(Long userId) {
        User user = userService.getUserById(userId);
        return cardRepository.findByUser(user).stream()
                .map(card -> new CardResponse(card.getId(), card.getCardNumber(), card.getHolderName(), card.getExpirationDate()))
                .collect(Collectors.toList());
    }

    /**
     * Obtiene una tarjeta por su id y el usuario propietario.
     *
     * @param userId identificador del usuario
     * @param cardId identificador de la tarjeta
     * @return DTO de la tarjeta
     */
    public CardResponse getCard(Long userId, Long cardId) {
        User user = userService.getUserById(userId);
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));
        return new CardResponse(card.getId(), card.getCardNumber(), card.getHolderName(), card.getExpirationDate());
    }

    /**
     * Crea y asocia una nueva tarjeta a un usuario.
     *
     * @param userId identificador del usuario
     * @param request datos de la tarjeta
     * @return DTO de la tarjeta creada
     */
    public CardResponse addCard(Long userId, CreateCardRequest request) {
        User user = userService.getUserById(userId);
        // Comprobar si el número de tarjeta ya existe
        if (cardRepository.existsByCardNumber(request.getCardNumber())) {
            throw new DataIntegrityViolationException("Card number already exists");
        }
        Card card = new Card();
        card.setUser(user);
        card.setCardNumber(request.getCardNumber());
        card.setHolderName(request.getHolderName());
        card.setExpirationDate(request.getExpirationDate());
        Card saved = cardRepository.save(card);
        return new CardResponse(saved.getId(), saved.getCardNumber(), saved.getHolderName(), saved.getExpirationDate());
    }

    /**
     * Elimina una tarjeta asociada a un usuario.
     *
     * @param userId identificador del usuario
     * @param cardId identificador de la tarjeta
     */
    public void deleteCard(Long userId, Long cardId) {
        User user = userService.getUserById(userId);
        Card card = cardRepository.findByIdAndUser(cardId, user)
                .orElseThrow(() -> new NoSuchElementException("Card not found"));
        cardRepository.delete(card);
    }
}