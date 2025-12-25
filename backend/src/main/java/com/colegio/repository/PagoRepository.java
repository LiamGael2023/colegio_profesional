package com.colegio.repository;

import com.colegio.model.entity.Pago;
import com.colegio.model.enums.MetodoPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Pago
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, Long> {

    /**
     * Busca pagos por persona
     */
    List<Pago> findByPersonaIdOrderByFechaPagoDesc(Long personaId);

    /**
     * Busca un pago por número de comprobante
     */
    Optional<Pago> findByNumeroComprobante(String numeroComprobante);

    /**
     * Busca pagos por método de pago
     */
    List<Pago> findByMetodoPago(MetodoPago metodoPago);

    /**
     * Busca pagos en un rango de fechas
     */
    @Query("SELECT p FROM Pago p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin ORDER BY p.fechaPago DESC")
    List<Pago> findPagosPorRangoFechas(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtiene el total de pagos de una persona
     */
    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Pago p WHERE p.persona.id = :personaId")
    BigDecimal getTotalPagosPorPersona(@Param("personaId") Long personaId);

    /**
     * Obtiene el total recaudado en un rango de fechas
     */
    @Query("SELECT COALESCE(SUM(p.montoTotal), 0) FROM Pago p WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin")
    BigDecimal getTotalRecaudadoEnPeriodo(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Busca pagos por usuario que registró
     */
    List<Pago> findByUsuarioRegistro(String usuarioRegistro);

    /**
     * Obtiene los últimos N pagos
     */
    List<Pago> findTop10ByOrderByFechaPagoDesc();

    /**
     * Busca pagos de hoy
     */
    @Query("SELECT p FROM Pago p WHERE DATE(p.fechaPago) = CURRENT_DATE ORDER BY p.fechaPago DESC")
    List<Pago> findPagosDelDia();

    /**
     * Obtiene estadísticas de pagos por método
     */
    @Query("SELECT p.metodoPago, COUNT(p), SUM(p.montoTotal) " +
           "FROM Pago p " +
           "WHERE p.fechaPago BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY p.metodoPago")
    List<Object[]> getEstadisticasPorMetodoPago(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );
}
