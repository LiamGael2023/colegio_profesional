package com.colegio.model.enums;

/**
 * Método de pago
 */
public enum MetodoPago {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta"),
    TRANSFERENCIA("Transferencia"),
    CHEQUE("Cheque"),
    YAPE("Yape"),
    PLIN("Plin");

    private final String descripcion;

    MetodoPago(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
