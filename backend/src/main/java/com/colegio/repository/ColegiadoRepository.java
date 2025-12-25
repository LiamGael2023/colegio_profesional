package com.colegio.repository;

import com.colegio.model.entity.Colegiado;
import com.colegio.model.enums.EstadoHabilitacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Colegiado
 */
@Repository
public interface ColegiadoRepository extends JpaRepository<Colegiado, Long> {

    /**
     * Busca un colegiado por número de colegiatura
     */
    Optional<Colegiado> findByNumeroColegiatura(String numeroColegiatura);

    /**
     * Busca un colegiado por ID de persona
     */
    Optional<Colegiado> findByPersonaId(Long personaId);

    /**
     * Verifica si existe un colegiado con el número de colegiatura
     */
    boolean existsByNumeroColegiatura(String numeroColegiatura);

    /**
     * Busca colegiados por estado de habilitación
     */
    List<Colegiado> findByEstadoHabilitacion(EstadoHabilitacion estadoHabilitacion);

    /**
     * Busca colegiados habilitados
     */
    @Query("SELECT c FROM Colegiado c WHERE c.estadoHabilitacion = 'HABILITADO' AND c.persona.activo = true")
    List<Colegiado> findColegiadosHabilitados();

    /**
     * Busca colegiados que deben ser inhabilitados
     */
    @Query("SELECT c FROM Colegiado c WHERE " +
           "c.estadoHabilitacion = 'HABILITADO' AND " +
           "c.mesesImpagosConsecutivos >= c.mesesToleranciaImpago")
    List<Colegiado> findColegiadosParaInhabilitar();

    /**
     * Busca colegiados por especialidad
     */
    List<Colegiado> findByEspecialidad(String especialidad);

    /**
     * Cuenta colegiados por estado de habilitación
     */
    long countByEstadoHabilitacion(EstadoHabilitacion estadoHabilitacion);

    /**
     * Busca colegiados con cuotas vencidas
     */
    @Query("SELECT DISTINCT c FROM Colegiado c " +
           "JOIN Aportacion a ON a.persona.id = c.persona.id " +
           "WHERE a.estado = 'PENDIENTE' AND a.fechaVencimiento < CURRENT_DATE")
    List<Colegiado> findColegiadosConCuotasVencidas();
}
