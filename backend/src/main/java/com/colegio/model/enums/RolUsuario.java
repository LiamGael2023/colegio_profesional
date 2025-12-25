package com.colegio.model.enums;

/**
 * Rol de usuario del sistema
 */
public enum RolUsuario {
    ADMIN("Administrador"),
    CAJERO("Cajero"),
    SECRETARIA("Secretaria"),
    AUDITOR("Auditor");

    private final String descripcion;

    RolUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
