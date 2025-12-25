package com.colegio.model.enums;

/**
 * Tipo de comprobante
 */
public enum TipoComprobante {
    BOLETA("Boleta"),
    FACTURA("Factura"),
    RECIBO("Recibo");

    private final String descripcion;

    TipoComprobante(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
