package com.colegio.model.entity;

import com.colegio.model.enums.MetodoPago;
import com.colegio.model.enums.TipoComprobante;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Entidad Pago
 * Registra los pagos realizados
 */
@Entity
@Table(name = "pagos", indexes = {
    @Index(name = "idx_persona_id", columnList = "persona_id"),
    @Index(name = "idx_fecha_pago", columnList = "fecha_pago"),
    @Index(name = "idx_numero_comprobante", columnList = "numero_comprobante")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pago {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "persona_id", nullable = false)
    private Persona persona;

    @Column(name = "monto_total", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoTotal;

    @Enumerated(EnumType.STRING)
    @Column(name = "metodo_pago", nullable = false)
    private MetodoPago metodoPago;

    @Column(name = "numero_comprobante", length = 50)
    private String numeroComprobante;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_comprobante", nullable = false)
    @Builder.Default
    private TipoComprobante tipoComprobante = TipoComprobante.RECIBO;

    @Column(columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_pago", nullable = false)
    @Builder.Default
    private LocalDateTime fechaPago = LocalDateTime.now();

    @Column(name = "usuario_registro", length = 100)
    private String usuarioRegistro;

    @OneToMany(mappedBy = "pago", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DetallePago> detalles = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Agrega un detalle de pago
     */
    public void agregarDetalle(DetallePago detalle) {
        detalles.add(detalle);
        detalle.setPago(this);
    }

    /**
     * Remueve un detalle de pago
     */
    public void removerDetalle(DetallePago detalle) {
        detalles.remove(detalle);
        detalle.setPago(null);
    }
}
