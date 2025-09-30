package com.dmh.backend.repository;

import com.dmh.backend.model.Transference;
import com.dmh.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para gestionar entidades {@link Transference}.
 */
@Repository
public interface TransferenceRepository extends JpaRepository<Transference, Long> {

    /**
     * Devuelve las últimas cinco transferencias realizadas por un usuario, ordenadas por fecha descendente.
     *
     * @param fromUser usuario emisor de la transferencia
     * @return lista de transferencias
     */
    List<Transference> findTop5ByFromUserOrderByDateDesc(User fromUser);
}