package com.colegio.controller;

import com.colegio.model.entity.Persona;
import com.colegio.service.PersonaService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador REST para gestión de personas
 */
@RestController
@RequestMapping("/personas")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class PersonaController {

    private final PersonaService personaService;

    /**
     * Crea una nueva persona
     * POST /personas
     */
    @PostMapping
    public ResponseEntity<Persona> crearPersona(@RequestBody Persona persona) {
        try {
            Persona nuevaPersona = personaService.crearPersona(persona);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevaPersona);
        } catch (Exception e) {
            log.error("Error creando persona: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Actualiza una persona
     * PUT /personas/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Persona> actualizarPersona(
            @PathVariable Long id,
            @RequestBody Persona persona
    ) {
        try {
            Persona personaActualizada = personaService.actualizarPersona(id, persona);
            return ResponseEntity.ok(personaActualizada);
        } catch (Exception e) {
            log.error("Error actualizando persona: {}", e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
    }

    /**
     * Obtiene una persona por ID
     * GET /personas/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Persona> obtenerPersonaPorId(@PathVariable Long id) {
        try {
            Persona persona = personaService.obtenerPersonaPorId(id);
            return ResponseEntity.ok(persona);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca una persona por DNI
     * GET /personas/dni/{dni}
     */
    @GetMapping("/dni/{dni}")
    public ResponseEntity<Persona> buscarPorDni(@PathVariable String dni) {
        try {
            Persona persona = personaService.buscarPorDni(dni);
            return ResponseEntity.ok(persona);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Busca personas por criterio (DNI o nombre)
     * GET /personas/buscar?criterio=xxx
     */
    @GetMapping("/buscar")
    public ResponseEntity<List<Persona>> buscarPersonas(@RequestParam String criterio) {
        List<Persona> personas = personaService.buscarPersonas(criterio);
        return ResponseEntity.ok(personas);
    }

    /**
     * Obtiene todas las personas activas
     * GET /personas/activas
     */
    @GetMapping("/activas")
    public ResponseEntity<List<Persona>> obtenerPersonasActivas() {
        List<Persona> personas = personaService.obtenerPersonasActivas();
        return ResponseEntity.ok(personas);
    }

    /**
     * Obtiene todas las personas
     * GET /personas
     */
    @GetMapping
    public ResponseEntity<List<Persona>> obtenerTodasLasPersonas() {
        List<Persona> personas = personaService.obtenerTodasLasPersonas();
        return ResponseEntity.ok(personas);
    }

    /**
     * Desactiva una persona
     * DELETE /personas/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> desactivarPersona(@PathVariable Long id) {
        try {
            personaService.desactivarPersona(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Error desactivando persona: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Activa una persona
     * PATCH /personas/{id}/activar
     */
    @PatchMapping("/{id}/activar")
    public ResponseEntity<Void> activarPersona(@PathVariable Long id) {
        try {
            personaService.activarPersona(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            log.error("Error activando persona: {}", e.getMessage());
            return ResponseEntity.notFound().build();
        }
    }
}
