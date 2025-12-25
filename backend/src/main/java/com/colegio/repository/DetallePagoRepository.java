package com.colegio.repository;

import com.colegio.model.entity.DetallePago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio para DetallePago
 */
@Repository
public interface DetallePagoRepository extends JpaRepository<DetallePago, Long> {

    /**
     * Busca detalles de pago por ID de pago
     */
    List<DetallePago> findByPagoId(Long pagoId);

    /**
     * Busca detalles de pago por ID de aportación
     */
    List<DetallePago> findByAportacionId(Long aportacionId);

    /**
     * Verifica si una aportación ya fue pagada
     */
    boolean existsByAportacionId(Long aportacionId);

    /**
     * Obtiene el historial de pagos de una persona
     */
    @Query("SELECT dp FROM DetallePago dp " +
           "JOIN dp.pago p " +
           "WHERE p.persona.id = :personaId " +
           "ORDER BY p.fechaPago DESC")
    List<DetallePago> findHistorialPagosPorPersona(@Param("personaId") Long personaId);
}
