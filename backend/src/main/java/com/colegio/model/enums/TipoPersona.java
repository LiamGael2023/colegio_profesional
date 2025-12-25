package com.colegio.model.enums;

/**
 * Tipo de persona en el sistema
 */
public enum TipoPersona {
    PUBLICO("Público General"),
    COLEGIADO("Colegiado");

    private final String descripcion;

    TipoPersona(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
