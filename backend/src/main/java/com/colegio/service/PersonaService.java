package com.colegio.service;

import com.colegio.model.entity.Persona;
import com.colegio.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de personas
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PersonaService {

    private final PersonaRepository personaRepository;

    /**
     * Crea una nueva persona
     */
    @Transactional
    public Persona crearPersona(Persona persona) {
        log.info("Creando persona con DNI: {}", persona.getDni());

        // Validar que no existe el DNI
        if (personaRepository.existsByDni(persona.getDni())) {
            throw new RuntimeException("Ya existe una persona con DNI: " + persona.getDni());
        }

        return personaRepository.save(persona);
    }

    /**
     * Actualiza una persona
     */
    @Transactional
    public Persona actualizarPersona(Long id, Persona personaActualizada) {
        log.info("Actualizando persona ID: {}", id);

        Persona persona = obtenerPersonaPorId(id);

        // Validar cambio de DNI
        if (!persona.getDni().equals(personaActualizada.getDni())) {
            if (personaRepository.existsByDni(personaActualizada.getDni())) {
                throw new RuntimeException("Ya existe una persona con DNI: " + personaActualizada.getDni());
            }
            persona.setDni(personaActualizada.getDni());
        }

        persona.setNombres(personaActualizada.getNombres());
        persona.setApellidoPaterno(personaActualizada.getApellidoPaterno());
        persona.setApellidoMaterno(personaActualizada.getApellidoMaterno());
        persona.setEmail(personaActualizada.getEmail());
        persona.setTelefono(personaActualizada.getTelefono());
        persona.setDireccion(personaActualizada.getDireccion());
        persona.setFechaNacimiento(personaActualizada.getFechaNacimiento());

        return personaRepository.save(persona);
    }

    /**
     * Obtiene una persona por ID
     */
    public Persona obtenerPersonaPorId(Long id) {
        return personaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + id));
    }

    /**
     * Busca una persona por DNI
     */
    public Persona buscarPorDni(String dni) {
        return personaRepository.findByDni(dni)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada con DNI: " + dni));
    }

    /**
     * Busca personas por criterio (DNI o nombre)
     */
    public List<Persona> buscarPersonas(String criterio) {
        return personaRepository.buscarPorDniONombre(criterio);
    }

    /**
     * Obtiene todas las personas activas
     */
    public List<Persona> obtenerPersonasActivas() {
        return personaRepository.findByActivoTrue();
    }

    /**
     * Obtiene todas las personas
     */
    public List<Persona> obtenerTodasLasPersonas() {
        return personaRepository.findAll();
    }

    /**
     * Desactiva una persona (borrado lógico)
     */
    @Transactional
    public void desactivarPersona(Long id) {
        Persona persona = obtenerPersonaPorId(id);
        persona.setActivo(false);
        personaRepository.save(persona);
        log.info("Persona ID: {} desactivada", id);
    }

    /**
     * Activa una persona
     */
    @Transactional
    public void activarPersona(Long id) {
        Persona persona = obtenerPersonaPorId(id);
        persona.setActivo(true);
        personaRepository.save(persona);
        log.info("Persona ID: {} activada", id);
    }
}
