package com.colegio.repository;

import com.colegio.model.entity.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para Configuracion
 */
@Repository
public interface ConfiguracionRepository extends JpaRepository<Configuracion, Long> {

    /**
     * Busca una configuración por clave
     */
    Optional<Configuracion> findByClave(String clave);

    /**
     * Verifica si existe una configuración con la clave
     */
    boolean existsByClave(String clave);
}
