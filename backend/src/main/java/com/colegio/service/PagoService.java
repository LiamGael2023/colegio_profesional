package com.colegio.service;

import com.colegio.model.entity.*;
import com.colegio.model.enums.EstadoAportacion;
import com.colegio.model.enums.MetodoPago;
import com.colegio.model.enums.TipoComprobante;
import com.colegio.repository.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Servicio para gestión de pagos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PagoService {

    private final PagoRepository pagoRepository;
    private final AportacionRepository aportacionRepository;
    private final DetallePagoRepository detallePagoRepository;
    private final PersonaRepository personaRepository;
    private final ColegiadoRepository colegiadoRepository;

    /**
     * Procesa un pago para múltiples aportaciones
     */
    @Transactional
    public Pago procesarPago(
        Long personaId,
        List<Long> aportacionesIds,
        MetodoPago metodoPago,
        TipoComprobante tipoComprobante,
        String numeroComprobante,
        String observaciones,
        String usuarioRegistro
    ) {
        log.info("Procesando pago para persona ID: {}, {} aportaciones", personaId, aportacionesIds.size());

        // Validar que la persona existe
        Persona persona = personaRepository.findById(personaId)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        // Obtener las aportaciones
        List<Aportacion> aportaciones = aportacionRepository.findByIdIn(aportacionesIds);

        if (aportaciones.isEmpty()) {
            throw new RuntimeException("No se encontraron aportaciones para pagar");
        }

        // Validar que todas las aportaciones pertenecen a la persona
        aportaciones.forEach(aportacion -> {
            if (!aportacion.getPersona().getId().equals(personaId)) {
                throw new RuntimeException("La aportación ID " + aportacion.getId() + " no pertenece a la persona");
            }
            if (!aportacion.isPendiente()) {
                throw new RuntimeException("La aportación ID " + aportacion.getId() + " no está pendiente");
            }
        });

        // Calcular el monto total
        BigDecimal montoTotal = aportaciones.stream()
            .map(Aportacion::getMonto)
            .reduce(BigDecimal.ZERO, BigDecimal::add);

        // Crear el pago
        Pago pago = Pago.builder()
            .persona(persona)
            .montoTotal(montoTotal)
            .metodoPago(metodoPago)
            .tipoComprobante(tipoComprobante)
            .numeroComprobante(numeroComprobante)
            .observaciones(observaciones)
            .fechaPago(LocalDateTime.now())
            .usuarioRegistro(usuarioRegistro)
            .build();

        pago = pagoRepository.save(pago);

        // Crear detalles de pago y marcar aportaciones como pagadas
        for (Aportacion aportacion : aportaciones) {
            DetallePago detalle = DetallePago.builder()
                .pago(pago)
                .aportacion(aportacion)
                .montoAplicado(aportacion.getMonto())
                .build();

            detallePagoRepository.save(detalle);

            // Marcar aportación como pagada
            aportacion.marcarComoPagada();
            aportacionRepository.save(aportacion);
        }

        log.info("Pago procesado exitosamente. ID: {}, Monto: {}", pago.getId(), montoTotal);

        // Actualizar estado de habilitación del colegiado si aplica
        colegiadoRepository.findByPersonaId(personaId).ifPresent(colegiado -> {
            actualizarEstadoHabilitacionDespuesPago(colegiado);
        });

        return pago;
    }

    /**
     * Obtiene el historial de pagos de una persona
     */
    public List<Pago> obtenerHistorialPagos(Long personaId) {
        return pagoRepository.findByPersonaIdOrderByFechaPagoDesc(personaId);
    }

    /**
     * Obtiene un pago por ID
     */
    public Pago obtenerPagoPorId(Long pagoId) {
        return pagoRepository.findById(pagoId)
            .orElseThrow(() -> new RuntimeException("Pago no encontrado con ID: " + pagoId));
    }

    /**
     * Obtiene los detalles de un pago
     */
    public List<DetallePago> obtenerDetallesPago(Long pagoId) {
        return detallePagoRepository.findByPagoId(pagoId);
    }

    /**
     * Obtiene el total pagado por una persona
     */
    public BigDecimal obtenerTotalPagado(Long personaId) {
        return pagoRepository.getTotalPagosPorPersona(personaId);
    }

    /**
     * Obtiene los pagos del día
     */
    public List<Pago> obtenerPagosDelDia() {
        return pagoRepository.findPagosDelDia();
    }

    /**
     * Obtiene el total recaudado en un período
     */
    public BigDecimal obtenerTotalRecaudado(LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        return pagoRepository.getTotalRecaudadoEnPeriodo(fechaInicio, fechaFin);
    }

    /**
     * Actualiza el estado de habilitación después de un pago
     * Si el colegiado tenía meses impagos, se reinicia el contador
     */
    private void actualizarEstadoHabilitacionDespuesPago(Colegiado colegiado) {
        // Contar aportaciones pendientes
        long aportacionesPendientes = aportacionRepository.countByPersonaIdAndEstado(
            colegiado.getPersona().getId(),
            EstadoAportacion.PENDIENTE
        );

        if (aportacionesPendientes == 0) {
            // No hay deudas, reiniciar contador
            colegiado.reiniciarMesesImpagos();
            colegiadoRepository.save(colegiado);
            log.info("Colegiado {} - Contador de meses impagos reiniciado", colegiado.getNumeroColegiatura());
        } else {
            // Aún hay deudas, actualizar contador
            colegiado.setMesesImpagosConsecutivos((int) aportacionesPendientes);
            colegiadoRepository.save(colegiado);
            log.info("Colegiado {} - Meses impagos actualizados: {}",
                colegiado.getNumeroColegiatura(), aportacionesPendientes);
        }
    }
}
