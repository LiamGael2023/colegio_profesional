package com.colegio.model.enums;

/**
 * Estado de una aportación
 */
public enum EstadoAportacion {
    PENDIENTE("Pendiente"),
    PAGADO("Pagado"),
    ANULADO("Anulado");

    private final String descripcion;

    EstadoAportacion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
