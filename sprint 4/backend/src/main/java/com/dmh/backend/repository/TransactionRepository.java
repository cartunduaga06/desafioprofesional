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

    /**
     * Devuelve todas las transacciones de un usuario, ordenadas de más reciente a más antigua.
     * Utilizado para mostrar el historial completo de actividad.
     *
     * @param user usuario propietario de las transacciones
     * @return lista de transacciones
     */
    List<Transaction> findByUserOrderByDateDesc(User user);

    /**
     * Busca una transacción por su id y usuario. Sirve para asegurar que un usuario sólo pueda acceder
     * a sus propias transacciones.
     *
     * @param id    identificador de la transacción
     * @param user  usuario propietario
     * @return una transacción si existe
     */
    java.util.Optional<Transaction> findByIdAndUser(Long id, User user);
}