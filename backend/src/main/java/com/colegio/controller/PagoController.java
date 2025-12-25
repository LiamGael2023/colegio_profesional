package com.colegio.controller;

import com.colegio.model.entity.DetallePago;
import com.colegio.model.entity.Pago;
import com.colegio.model.enums.MetodoPago;
import com.colegio.model.enums.TipoComprobante;
import com.colegio.service.PagoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de pagos
 */
@RestController
@RequestMapping("/pagos")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PagoController {

    private final PagoService pagoService;

    /**
     * Procesa un pago
     * POST /pagos
     */
    @PostMapping
    public ResponseEntity<Pago> procesarPago(
            @RequestBody Map<String, Object> request,
            Authentication authentication
    ) {
        try {
            Long personaId = Long.valueOf(request.get("personaId").toString());

            @SuppressWarnings("unchecked")
            List<Long> aportacionesIds = ((List<?>) request.get("aportacionesIds"))
                    .stream()
                    .map(id -> Long.valueOf(id.toString()))
                    .toList();

            MetodoPago metodoPago = MetodoPago.valueOf(request.get("metodoPago").toString());

            TipoComprobante tipoComprobante = request.containsKey("tipoComprobante")
                    ? TipoComprobante.valueOf(request.get("tipoComprobante").toString())
                    : TipoComprobante.RECIBO;

            String numeroComprobante = request.containsKey("numeroComprobante")
                    ? request.get("numeroComprobante").toString()
                    : null;

            String observaciones = request.containsKey("observaciones")
                    ? request.get("observaciones").toString()
                    : null;

            String usuarioRegistro = authentication != null
                    ? authentication.getName()
                    : "SISTEMA";

            Pago pago = pagoService.procesarPago(
                    personaId,
                    aportacionesIds,
                    metodoPago,
                    tipoComprobante,
                    numeroComprobante,
                    observaciones,
                    usuarioRegistro
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(pago);
        } catch (Exception e) {
            log.error("Error procesando pago: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtiene el historial de pagos de una persona
     * GET /pagos/persona/{personaId}
     */
    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<Pago>> obtenerHistorialPagos(@PathVariable Long personaId) {
        List<Pago> pagos = pagoService.obtenerHistorialPagos(personaId);
        return ResponseEntity.ok(pagos);
    }

    /**
     * Obtiene un pago por ID
     * GET /pagos/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Pago> obtenerPagoPorId(@PathVariable Long id) {
        try {
            Pago pago = pagoService.obtenerPagoPorId(id);
            return ResponseEntity.ok(pago);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene los detalles de un pago
     * GET /pagos/{id}/detalles
     */
    @GetMapping("/{id}/detalles")
    public ResponseEntity<List<DetallePago>> obtenerDetallesPago(@PathVariable Long id) {
        List<DetallePago> detalles = pagoService.obtenerDetallesPago(id);
        return ResponseEntity.ok(detalles);
    }

    /**
     * Obtiene el total pagado por una persona
     * GET /pagos/total/{personaId}
     */
    @GetMapping("/total/{personaId}")
    public ResponseEntity<?> obtenerTotalPagado(@PathVariable Long personaId) {
        BigDecimal total = pagoService.obtenerTotalPagado(personaId);
        return ResponseEntity.ok(Map.of(
                "personaId", personaId,
                "totalPagado", total
        ));
    }

    /**
     * Obtiene los pagos del día
     * GET /pagos/hoy
     */
    @GetMapping("/hoy")
    public ResponseEntity<List<Pago>> obtenerPagosDelDia() {
        List<Pago> pagos = pagoService.obtenerPagosDelDia();
        return ResponseEntity.ok(pagos);
    }

    /**
     * Obtiene el total recaudado en un período
     * GET /pagos/recaudado?fechaInicio=xxx&fechaFin=xxx
     */
    @GetMapping("/recaudado")
    public ResponseEntity<?> obtenerTotalRecaudado(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fechaFin
    ) {
        BigDecimal total = pagoService.obtenerTotalRecaudado(fechaInicio, fechaFin);
        return ResponseEntity.ok(Map.of(
                "fechaInicio", fechaInicio,
                "fechaFin", fechaFin,
                "totalRecaudado", total
        ));
    }
}
