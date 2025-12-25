package com.colegio.model.entity;

import com.colegio.model.enums.EstadoAportacion;
import com.colegio.model.enums.TipoGeneracion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Aportacion
 * Registra las obligaciones/deudas mensuales
 */
@Entity
@Table(name = "aportaciones",
    uniqueConstraints = {
        @UniqueConstraint(name = "unique_aportacion", columnNames = {"persona_id", "mes", "anio"})
    },
    indexes = {
        @Index(name = "idx_persona_estado", columnList = "persona_id, estado"),
        @Index(name = "idx_mes_anio", columnList = "mes, anio"),
        @Index(name = "idx_estado", columnList = "estado"),
        @Index(name = "idx_fecha_vencimiento", columnList = "fecha_vencimiento")
    }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aportacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(nullable = false)
    private Integer mes;

    @Column(nullable = false)
    private Integer anio;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @Column(nullable = false, length = 255)
    @Builder.Default
    private String concepto = "Cuota mensual de colegiatura";

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Builder.Default
    private EstadoAportacion estado = EstadoAportacion.PENDIENTE;

    @Column(name = "fecha_vencimiento")
    private LocalDate fechaVencimiento;

    @Column(name = "fecha_generacion", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaGeneracion;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_generacion", nullable = false)
    @Builder.Default
    private TipoGeneracion tipoGeneracion = TipoGeneracion.AUTOMATICA;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Verifica si la aportación está pendiente de pago
     */
    @Transient
    public boolean isPendiente() {
        return EstadoAportacion.PENDIENTE.equals(estado);
    }

    /**
     * Verifica si la aportación está pagada
     */
    @Transient
    public boolean isPagada() {
        return EstadoAportacion.PAGADO.equals(estado);
    }

    /**
     * Verifica si la aportación está vencida
     */
    @Transient
    public boolean isVencida() {
        if (fechaVencimiento == null || !isPendiente()) {
            return false;
        }
        return LocalDate.now().isAfter(fechaVencimiento);
    }

    /**
     * Marca la aportación como pagada
     */
    public void marcarComoPagada() {
        this.estado = EstadoAportacion.PAGADO;
    }

    /**
     * Anula la aportación
     */
    public void anular() {
        this.estado = EstadoAportacion.ANULADO;
    }
}
