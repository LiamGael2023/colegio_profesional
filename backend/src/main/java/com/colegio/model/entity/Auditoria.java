package com.colegio.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * Entidad Auditoria
 * Log de operaciones importantes del sistema
 */
@Entity
@Table(name = "auditoria", indexes = {
    @Index(name = "idx_usuario_id", columnList = "usuario_id"),
    @Index(name = "idx_fecha_hora", columnList = "fecha_hora"),
    @Index(name = "idx_accion", columnList = "accion"),
    @Index(name = "idx_tabla_registro", columnList = "tabla_afectada, registro_id")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Auditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @Column(nullable = false, length = 100)
    private String accion;

    @Column(name = "tabla_afectada", length = 50)
    private String tablaAfectada;

    @Column(name = "registro_id")
    private Long registroId;

    @Column(name = "datos_anteriores", columnDefinition = "JSON")
    private String datosAnteriores;

    @Column(name = "datos_nuevos", columnDefinition = "JSON")
    private String datosNuevos;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", length = 255)
    private String userAgent;

    @CreationTimestamp
    @Column(name = "fecha_hora", nullable = false, updatable = false)
    private LocalDateTime fechaHora;
}
