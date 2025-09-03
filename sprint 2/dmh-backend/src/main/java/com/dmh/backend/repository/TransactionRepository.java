package com.dmh.backend.repository;

import com.dmh.backend.model.Transaction;
import com.dmh.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio de transacciones.
 */
@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {

    /**
     * Obtiene las últimas cinco transacciones de un usuario, ordenadas de más reciente a más antigua.
     *
     * @param user usuario propietario de las transacciones
     * @return lista de transacciones (hasta 5)
     */
    List<Transaction> findTop5ByUserOrderByDateDesc(User user);
}