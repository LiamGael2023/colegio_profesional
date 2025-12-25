package com.colegio.model.enums;

/**
 * Estado de habilitación de un colegiado
 */
public enum EstadoHabilitacion {
    HABILITADO("Habilitado"),
    INHABILITADO("Inhabilitado"),
    SUSPENDIDO("Suspendido");

    private final String descripcion;

    EstadoHabilitacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
