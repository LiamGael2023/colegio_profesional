package com.colegio.repository;

import com.colegio.model.entity.Aportacion;
import com.colegio.model.enums.EstadoAportacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Aportacion
 */
@Repository
public interface AportacionRepository extends JpaRepository<Aportacion, Long> {

    /**
     * Verifica si existe una aportación para persona, mes y año
     * (Evita duplicados - UNIQUE KEY compuesta)
     */
    boolean existsByPersonaIdAndMesAndAnio(Long personaId, Integer mes, Integer anio);

    /**
     * Busca aportaciones de una persona
     */
    List<Aportacion> findByPersonaId(Long personaId);

    /**
     * Busca aportaciones de una persona ordenadas por año y mes descendente
     */
    List<Aportacion> findByPersonaIdOrderByAnioDescMesDesc(Long personaId);

    /**
     * Busca aportaciones por estado
     */
    List<Aportacion> findByEstado(EstadoAportacion estado);

    /**
     * Busca aportaciones pendientes de una persona
     */
    List<Aportacion> findByPersonaIdAndEstadoOrderByAnioAscMesAsc(Long personaId, EstadoAportacion estado);

    /**
     * Busca aportaciones pendientes de una persona
     */
    @Query("SELECT a FROM Aportacion a WHERE a.persona.id = :personaId AND a.estado = 'PENDIENTE' ORDER BY a.anio, a.mes")
    List<Aportacion> findAportacionesPendientesPorPersona(@Param("personaId") Long personaId);

    /**
     * Busca aportaciones de un mes y año específicos
     */
    List<Aportacion> findByMesAndAnio(Integer mes, Integer anio);

    /**
     * Busca aportaciones vencidas
     */
    @Query("SELECT a FROM Aportacion a WHERE a.estado = 'PENDIENTE' AND a.fechaVencimiento < :fecha")
    List<Aportacion> findAportacionesVencidas(@Param("fecha") LocalDate fecha);

    /**
     * Obtiene el total de deuda pendiente de una persona
     */
    @Query("SELECT COALESCE(SUM(a.monto), 0) FROM Aportacion a WHERE a.persona.id = :personaId AND a.estado = 'PENDIENTE'")
    BigDecimal getTotalDeudaPendiente(@Param("personaId") Long personaId);

    /**
     * Cuenta aportaciones pendientes de una persona
     */
    long countByPersonaIdAndEstado(Long personaId, EstadoAportacion estado);

    /**
     * Busca aportaciones por IDs
     */
    List<Aportacion> findByIdIn(List<Long> ids);

    /**
     * Busca aportación específica de persona, mes y año
     */
    Optional<Aportacion> findByPersonaIdAndMesAndAnio(Long personaId, Integer mes, Integer anio);

    /**
     * Obtiene estadísticas de recaudación por mes y año
     */
    @Query("SELECT a.mes, a.anio, COUNT(a), SUM(a.monto) " +
           "FROM Aportacion a " +
           "WHERE a.estado = 'PAGADO' AND a.anio = :anio " +
           "GROUP BY a.mes, a.anio " +
           "ORDER BY a.mes")
    List<Object[]> getEstadisticasRecaudacion(@Param("anio") Integer anio);

    /**
     * Obtiene aportaciones pendientes de colegiados habilitados
     */
    @Query("SELECT a FROM Aportacion a " +
           "JOIN Colegiado c ON c.persona.id = a.persona.id " +
           "WHERE a.estado = 'PENDIENTE' AND c.estadoHabilitacion = 'HABILITADO'")
    List<Aportacion> findAportacionesPendientesDeHabilitados();
}
