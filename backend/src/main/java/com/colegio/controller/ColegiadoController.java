package com.colegio.controller;

import com.colegio.model.entity.Colegiado;
import com.colegio.model.enums.EstadoHabilitacion;
import com.colegio.service.ColegiadoService;
import com.colegio.service.HabilitacionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * Controlador REST para gestión de colegiados
 */
@RestController
@RequestMapping("/colegiados")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ColegiadoController {

    private final ColegiadoService colegiadoService;
    private final HabilitacionService habilitacionService;

    /**
     * Convierte una persona a colegiado
     * POST /colegiados/convertir
     */
    @PostMapping("/convertir")
    public ResponseEntity<Colegiado> convertirPersonaAColegiado(
            @RequestBody Map<String, Object> request
    ) {
        try {
            Long personaId = Long.valueOf(request.get("personaId").toString());
            String numeroColegiatura = request.get("numeroColegiatura").toString();
            String especialidad = request.get("especialidad").toString();
            String universidad = request.get("universidad").toString();
            LocalDate fechaColegiatura = request.containsKey("fechaColegiatura")
                ? LocalDate.parse(request.get("fechaColegiatura").toString())
                : LocalDate.now();

            Colegiado colegiado = colegiadoService.convertirPersonaAColegiado(
                    personaId,
                    numeroColegiatura,
                    especialidad,
                    universidad,
                    fechaColegiatura
            );

            return ResponseEntity.status(HttpStatus.CREATED).body(colegiado);
        } catch (Exception e) {
            log.error("Error convirtiendo persona a colegiado: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtiene un colegiado por ID
     * GET /colegiados/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Colegiado> obtenerColegiadoPorId(@PathVariable Long id) {
        try {
            Colegiado colegiado = colegiadoService.obtenerColegiadoPorId(id);
            return ResponseEntity.ok(colegiado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene un colegiado por número de colegiatura
     * GET /colegiados/numero/{numeroColegiatura}
     */
    @GetMapping("/numero/{numeroColegiatura}")
    public ResponseEntity<Colegiado> obtenerPorNumeroColegiatura(@PathVariable String numeroColegiatura) {
        try {
            Colegiado colegiado = colegiadoService.obtenerPorNumeroColegiatura(numeroColegiatura);
            return ResponseEntity.ok(colegiado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene un colegiado por ID de persona
     * GET /colegiados/persona/{personaId}
     */
    @GetMapping("/persona/{personaId}")
    public ResponseEntity<Colegiado> obtenerPorPersonaId(@PathVariable Long personaId) {
        try {
            Colegiado colegiado = colegiadoService.obtenerPorPersonaId(personaId);
            return ResponseEntity.ok(colegiado);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Obtiene todos los colegiados
     * GET /colegiados
     */
    @GetMapping
    public ResponseEntity<List<Colegiado>> obtenerTodosLosColegiados() {
        List<Colegiado> colegiados = colegiadoService.obtenerTodosLosColegiados();
        return ResponseEntity.ok(colegiados);
    }

    /**
     * Obtiene colegiados habilitados
     * GET /colegiados/habilitados
     */
    @GetMapping("/habilitados")
    public ResponseEntity<List<Colegiado>> obtenerColegiadosHabilitados() {
        List<Colegiado> colegiados = colegiadoService.obtenerColegiadosHabilitados();
        return ResponseEntity.ok(colegiados);
    }

    /**
     * Obtiene colegiados por estado de habilitación
     * GET /colegiados/estado/{estado}
     */
    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<Colegiado>> obtenerPorEstado(@PathVariable EstadoHabilitacion estado) {
        List<Colegiado> colegiados = colegiadoService.obtenerPorEstadoHabilitacion(estado);
        return ResponseEntity.ok(colegiados);
    }

    /**
     * Actualiza un colegiado
     * PUT /colegiados/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Colegiado> actualizarColegiado(
            @PathVariable Long id,
            @RequestBody Colegiado colegiado
    ) {
        try {
            Colegiado colegiadoActualizado = colegiadoService.actualizarColegiado(id, colegiado);
            return ResponseEntity.ok(colegiadoActualizado);
        } catch (Exception e) {
            log.error("Error actualizando colegiado: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Habilita un colegiado manualmente
     * PATCH /colegiados/{id}/habilitar
     */
    @PatchMapping("/{id}/habilitar")
    public ResponseEntity<Void> habilitarColegiado(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String observaciones = body != null ? body.get("observaciones") : "Habilitado manualmente";
            habilitacionService.habilitarColegiado(id, observaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error habilitando colegiado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Inhabilita un colegiado manualmente
     * PATCH /colegiados/{id}/inhabilitar
     */
    @PatchMapping("/{id}/inhabilitar")
    public ResponseEntity<Void> inhabilitarColegiado(
            @PathVariable Long id,
            @RequestBody(required = false) Map<String, String> body
    ) {
        try {
            String observaciones = body != null ? body.get("observaciones") : "Inhabilitado manualmente";
            habilitacionService.inhabilitarColegiado(id, observaciones);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error inhabilitando colegiado: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Obtiene colegiados con cuotas vencidas
     * GET /colegiados/con-cuotas-vencidas
     */
    @GetMapping("/con-cuotas-vencidas")
    public ResponseEntity<List<Colegiado>> obtenerColegiadosConCuotasVencidas() {
        List<Colegiado> colegiados = colegiadoService.obtenerColegiadosConCuotasVencidas();
        return ResponseEntity.ok(colegiados);
    }
}
