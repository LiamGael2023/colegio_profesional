package com.colegio.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Entidad DetallePago
 * Detalle de qué aportaciones cubre cada pago
 * Relación N:N entre Pago y Aportacion
 */
@Entity
@Table(name = "detalle_pagos",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_detalle", columnNames = {"pago_id", "aportacion_id"})
    },
    indexes = {
        @Index(name = "idx_pago_id", columnList = "pago_id"),
        @Index(name = "idx_aportacion_id", columnList = "aportacion_id")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetallePago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pago_id", nullable = false)
    private Pago pago;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aportacion_id", nullable = false)
    private Aportacion aportacion;

    @Column(name = "monto_aplicado", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAplicado;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
}
