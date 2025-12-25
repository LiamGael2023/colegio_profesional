package com.colegio.controller;

import com.colegio.model.entity.Aportacion;
import com.colegio.service.AportacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de aportaciones
 */
@RestController
@RequestMapping("/aportaciones")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class AportacionController {

    private final AportacionService aportacionService;

    /**
     * Genera cuotas manualmente para un mes y año
     * POST /aportaciones/generar
     * Requiere rol ADMIN
     */
    @PostMapping("/generar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> generarCuotasManual(@RequestBody Map<String, Integer> request) {
        try {
            Integer mes = request.get("mes");
            Integer anio = request.get("anio");

            int cuotasGeneradas = aportacionService.generarCuotasParaMesAnio(
                    mes, anio, com.colegio.model.enums.TipoGeneracion.MANUAL
            );

            return ResponseEntity.ok(Map.of(
                    "mensaje", "Cuotas generadas exitosamente",
                    "cuotasGeneradas", cuotasGeneradas,
                    "mes", mes,
                    "anio", anio
            ));
        } catch (Exception e) {
            log.error("Error generando cuotas: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    /**
     * Crea una aportación manual (pago adelantado)
     * POST /aportaciones/manual
     */
    @PostMapping("/manual")
    public ResponseEntity<Aportacion> crearAportacionManual(@RequestBody Map<String, Object> request) {
        try {
            Long personaId = Long.valueOf(request.get("personaId").toString());
            Integer mes = Integer.valueOf(request.get("mes").toString());
            Integer anio = Integer.valueOf(request.get("anio").toString());
            BigDecimal monto = request.containsKey("monto")
                    ? new BigDecimal(request.get("monto").toString())
                    : null;
            String concepto = request.containsKey("concepto")
                    ? request.get("concepto").toString()
                    : null;

            Aportacion aportacion = aportacionService.crearAportacionManual(
                    personaId, mes, anio, monto, concepto
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(aportacion);
        } catch (Exception e) {
            log.error("Error creando aportación manual: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Crea aportaciones adelantadas (múltiples meses)
     * POST /aportaciones/adelantadas
     */
    @PostMapping("/adelantadas")
    public ResponseEntity<List<Aportacion>> crearAportacionesAdelantadas(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long personaId = Long.valueOf(request.get("personaId").toString());
            Integer mesInicio = Integer.valueOf(request.get("mesInicio").toString());
            Integer anioInicio = Integer.valueOf(request.get("anioInicio").toString());
            Integer cantidadMeses = Integer.valueOf(request.get("cantidadMeses").toString());

            List<Aportacion> aportaciones = aportacionService.crearAportacionesAdelantadas(
                    personaId, mesInicio, anioInicio, cantidadMeses
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(aportaciones);
        } catch (Exception e) {
            log.error("Error creando aportaciones adelantadas: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtiene aportaciones pendientes de una persona
     * GET /aportaciones/pendientes/{personaId}
     */
    @GetMapping("/pendientes/{personaId}")
    public ResponseEntity<List<Aportacion>> obtenerAportacionesPendientes(@PathVariable Long personaId) {
        List<Aportacion> aportaciones = aportacionService.obtenerAportacionesPendientes(personaId);
        return ResponseEntity.ok(aportaciones);
    }

    /**
     * Obtiene el total de deuda de una persona
     * GET /aportaciones/deuda/{personaId}
     */
    @GetMapping("/deuda/{personaId}")
    public ResponseEntity<?> obtenerTotalDeuda(@PathVariable Long personaId) {
        BigDecimal deuda = aportacionService.obtenerTotalDeuda(personaId);
        return ResponseEntity.ok(Map.of(
                "personaId", personaId,
                "totalDeuda", deuda
        ));
    }

    /**
     * Obtiene todas las aportaciones de una persona
     * GET /aportaciones/persona/{personaId}
     */
    @GetMapping("/persona/{personaId}")
    public ResponseEntity<List<Aportacion>> obtenerAportacionesPorPersona(@PathVariable Long personaId) {
        List<Aportacion> aportaciones = aportacionService.obtenerAportacionesPorPersona(personaId);
        return ResponseEntity.ok(aportaciones);
    }

    /**
     * Anula una aportación
     * DELETE /aportaciones/{id}
     * Requiere rol ADMIN
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> anularAportacion(@PathVariable Long id) {
        try {
            aportacionService.anularAportacion(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error anulando aportación: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }
}
