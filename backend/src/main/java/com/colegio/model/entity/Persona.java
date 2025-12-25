package com.colegio.model.entity;

import com.colegio.model.enums.TipoPersona;
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
 * Entidad Persona - Base del sistema
 * Puede ser "Público General" o "Colegiado"
 */
@Entity
@Table(name = "personas", indexes = {
    @Index(name = "idx_dni", columnList = "dni"),
    @Index(name = "idx_tipo_persona", columnList = "tipo_persona"),
    @Index(name = "idx_activo", columnList = "activo")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 20)
    private String dni;

    @Column(nullable = false, length = 100)
    private String nombres;

    @Column(name = "apellido_paterno", nullable = false, length = 100)
    private String apellidoPaterno;

    @Column(name = "apellido_materno", length = 100)
    private String apellidoMaterno;

    @Column(length = 150)
    private String email;

    @Column(length = 20)
    private String telefono;

    @Column(length = 255)
    private String direccion;

    @Column(name = "fecha_nacimiento")
    private LocalDate fechaNacimiento;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_persona", nullable = false)
    @Builder.Default
    private TipoPersona tipoPersona = TipoPersona.PUBLICO;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp
    private LocalDateTime fechaRegistro;

    @Column(nullable = false)
    @Builder.Default
    private Boolean activo = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    /**
     * Obtiene el nombre completo de la persona
     */
    @Transient
    public String getNombreCompleto() {
        StringBuilder nombre = new StringBuilder(nombres);
        nombre.append(" ").append(apellidoPaterno);
        if (apellidoMaterno != null && !apellidoMaterno.isEmpty()) {
            nombre.append(" ").append(apellidoMaterno);
        }
        return nombre.toString();
    }
}
