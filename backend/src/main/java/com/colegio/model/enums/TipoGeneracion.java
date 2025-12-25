package com.colegio.model.enums;

/**
 * Tipo de generación de una aportación
 */
public enum TipoGeneracion {
    AUTOMATICA("Automática"),
    MANUAL("Manual");

    private final String descripcion;

    TipoGeneracion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
