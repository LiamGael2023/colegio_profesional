package com.colegio.repository;

import com.colegio.model.entity.Usuario;
import com.colegio.model.enums.RolUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio para Usuario
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

    /**
     * Busca un usuario por username
     */
    Optional<Usuario> findByUsername(String username);

    /**
     * Busca un usuario por email
     */
    Optional<Usuario> findByEmail(String email);

    /**
     * Verifica si existe un usuario con el username
     */
    boolean existsByUsername(String username);

    /**
     * Verifica si existe un usuario con el email
     */
    boolean existsByEmail(String email);

    /**
     * Busca usuarios por rol
     */
    List<Usuario> findByRol(RolUsuario rol);

    /**
     * Busca usuarios activos
     */
    List<Usuario> findByActivoTrue();

    /**
     * Busca usuarios activos por rol
     */
    List<Usuario> findByRolAndActivoTrue(RolUsuario rol);

    /**
     * Cuenta usuarios por rol
     */
    long countByRol(RolUsuario rol);
}
