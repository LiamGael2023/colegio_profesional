package com.colegio.repository;

import com.colegio.model.entity.Persona;
import com.colegio.model.enums.TipoPersona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Persona
 */
@Repository
public interface PersonaRepository extends JpaRepository<Persona, Long> {

    /**
     * Busca una persona por DNI
     */
    Optional<Persona> findByDni(String dni);

    /**
     * Verifica si existe una persona con el DNI especificado
     */
    boolean existsByDni(String dni);

    /**
     * Busca personas por tipo
     */
    List<Persona> findByTipoPersona(TipoPersona tipoPersona);

    /**
     * Busca personas activas por tipo
     */
    List<Persona> findByTipoPersonaAndActivoTrue(TipoPersona tipoPersona);

    /**
     * Busca personas por nombre completo (búsqueda parcial)
     */
    @Query("SELECT p FROM Persona p WHERE " +
           "LOWER(CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', COALESCE(p.apellidoMaterno, ''))) LIKE LOWER(CONCAT('%', :nombre, '%'))")
    List<Persona> buscarPorNombreCompleto(@Param("nombre") String nombre);

    /**
     * Busca personas por DNI o nombre
     */
    @Query("SELECT p FROM Persona p WHERE " +
           "p.dni LIKE CONCAT('%', :criterio, '%') OR " +
           "LOWER(CONCAT(p.nombres, ' ', p.apellidoPaterno, ' ', COALESCE(p.apellidoMaterno, ''))) LIKE LOWER(CONCAT('%', :criterio, '%'))")
    List<Persona> buscarPorDniONombre(@Param("criterio") String criterio);

    /**
     * Obtiene todas las personas activas
     */
    List<Persona> findByActivoTrue();

    /**
     * Cuenta personas por tipo
     */
    long countByTipoPersona(TipoPersona tipoPersona);
}
