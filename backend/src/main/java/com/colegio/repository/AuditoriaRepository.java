package com.colegio.repository;

import com.colegio.model.entity.Auditoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repositorio para Auditoria
 */
@Repository
public interface AuditoriaRepository extends JpaRepository<Auditoria, Long> {

    /**
     * Busca auditorías por usuario
     */
    List<Auditoria> findByUsuarioIdOrderByFechaHoraDesc(Long usuarioId);

    /**
     * Busca auditorías por acción
     */
    List<Auditoria> findByAccion(String accion);

    /**
     * Busca auditorías por tabla afectada
     */
    List<Auditoria> findByTablaAfectada(String tablaAfectada);

    /**
     * Busca auditorías de un registro específico
     */
    List<Auditoria> findByTablaAfectadaAndRegistroIdOrderByFechaHoraDesc(String tablaAfectada, Long registroId);

    /**
     * Busca auditorías en un rango de fechas
     */
    @Query("SELECT a FROM Auditoria a WHERE a.fechaHora BETWEEN :fechaInicio AND :fechaFin ORDER BY a.fechaHora DESC")
    List<Auditoria> findAuditoriasPorRangoFechas(
        @Param("fechaInicio") LocalDateTime fechaInicio,
        @Param("fechaFin") LocalDateTime fechaFin
    );

    /**
     * Obtiene las últimas N auditorías
     */
    List<Auditoria> findTop50ByOrderByFechaHoraDesc();
}
