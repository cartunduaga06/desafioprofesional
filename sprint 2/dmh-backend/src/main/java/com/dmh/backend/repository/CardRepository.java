package com.dmh.backend.repository;

import com.dmh.backend.model.Card;
import com.dmh.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para la entidad {@link Card}.
 */
@Repository
public interface CardRepository extends JpaRepository<Card, Long> {

    List<Card> findByUser(User user);

    Optional<Card> findByIdAndUser(Long id, User user);

    boolean existsByCardNumber(String cardNumber);
}