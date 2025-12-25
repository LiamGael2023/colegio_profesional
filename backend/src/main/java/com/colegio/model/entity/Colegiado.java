package com.colegio.model.entity;

import com.colegio.model.enums.EstadoHabilitacion;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Entidad Colegiado
 * Extensión de Persona (relación 1:1)
 */
@Entity
@Table(name = "colegiados", indexes = {
    @Index(name = "idx_numero_colegiatura", columnList = "numero_colegiatura"),
    @Index(name = "idx_estado_habilitacion", columnList = "estado_habilitacion"),
    @Index(name = "idx_persona_id", columnList = "persona_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Colegiado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false, unique = true)
    private Persona persona;

    @Column(name = "numero_colegiatura", nullable = false, unique = true, length = 20)
    private String numeroColegiatura;

    @Column(length = 100)
    private String especialidad;

    @Column(length = 150)
    private String universidad;

    @Column(name = "fecha_colegiatura", nullable = false)
    private LocalDate fechaColegiatura;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado_habilitacion", nullable = false)
    @Builder.Default
    private EstadoHabilitacion estadoHabilitacion = EstadoHabilitacion.HABILITADO;

    @Column(name = "meses_tolerancia_impago", nullable = false)
    @Builder.Default
    private Integer mesesToleranciaImpago = 3;

    @Column(name = "meses_impagos_consecutivos", nullable = false)
    @Builder.Default
    private Integer mesesImpagosConsecutivos = 0;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Verifica si el colegiado está habilitado
     */
    @Transient
    public boolean isHabilitado() {
        return EstadoHabilitacion.HABILITADO.equals(estadoHabilitacion);
    }

    /**
     * Incrementa el contador de meses impagos
     */
    public void incrementarMesesImpagos() {
        this.mesesImpagosConsecutivos++;
    }

    /**
     * Reinicia el contador de meses impagos
     */
    public void reiniciarMesesImpagos() {
        this.mesesImpagosConsecutivos = 0;
    }

    /**
     * Verifica si debe ser inhabilitado por meses impagos
     */
    @Transient
    public boolean debeInhabilitarse() {
        return mesesImpagosConsecutivos >= mesesToleranciaImpago;
    }
}
