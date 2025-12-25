package com.colegio.service;

import com.colegio.model.entity.Colegiado;
import com.colegio.model.entity.Persona;
import com.colegio.model.enums.EstadoHabilitacion;
import com.colegio.model.enums.TipoPersona;
import com.colegio.repository.ColegiadoRepository;
import com.colegio.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

/**
 * Servicio para gestión de colegiados
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ColegiadoService {

    private final ColegiadoRepository colegiadoRepository;
    private final PersonaRepository personaRepository;

    /**
     * Convierte una persona a colegiado
     * Asigna número de colegiatura y cambia el tipo de persona
     */
    @Transactional
    public Colegiado convertirPersonaAColegiado(
            Long personaId,
            String numeroColegiatura,
            String especialidad,
            String universidad,
            LocalDate fechaColegiatura
    ) {
        log.info("Convirtiendo persona ID: {} a colegiado", personaId);

        // Validar que la persona existe
        Persona persona = personaRepository.findById(personaId)
                .orElseThrow(() -> new RuntimeException("Persona no encontrada"));

        // Validar que no es ya un colegiado
        if (colegiadoRepository.findByPersonaId(personaId).isPresent()) {
            throw new RuntimeException("La persona ya es un colegiado");
        }

        // Validar que el número de colegiatura no existe
        if (colegiadoRepository.existsByNumeroColegiatura(numeroColegiatura)) {
            throw new RuntimeException("El número de colegiatura ya existe: " + numeroColegiatura);
        }

        // Crear el colegiado
        Colegiado colegiado = Colegiado.builder()
                .persona(persona)
                .numeroColegiatura(numeroColegiatura)
                .especialidad(especialidad)
                .universidad(universidad)
                .fechaColegiatura(fechaColegiatura != null ? fechaColegiatura : LocalDate.now())
                .estadoHabilitacion(EstadoHabilitacion.HABILITADO)
                .mesesToleranciaImpago(3)
                .mesesImpagosConsecutivos(0)
                .build();

        colegiado = colegiadoRepository.save(colegiado);

        // Actualizar tipo de persona
        persona.setTipoPersona(TipoPersona.COLEGIADO);
        personaRepository.save(persona);

        log.info("Persona convertida a colegiado: {}", numeroColegiatura);
        return colegiado;
    }

    /**
     * Obtiene un colegiado por ID
     */
    public Colegiado obtenerColegiadoPorId(Long id) {
        return colegiadoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Colegiado no encontrado"));
    }

    /**
     * Obtiene un colegiado por número de colegiatura
     */
    public Colegiado obtenerPorNumeroColegiatura(String numeroColegiatura) {
        return colegiadoRepository.findByNumeroColegiatura(numeroColegiatura)
                .orElseThrow(() -> new RuntimeException("Colegiado no encontrado con número: " + numeroColegiatura));
    }

    /**
     * Obtiene un colegiado por ID de persona
     */
    public Colegiado obtenerPorPersonaId(Long personaId) {
        return colegiadoRepository.findByPersonaId(personaId)
                .orElseThrow(() -> new RuntimeException("No existe colegiado para la persona ID: " + personaId));
    }

    /**
     * Obtiene todos los colegiados
     */
    public List<Colegiado> obtenerTodosLosColegiados() {
        return colegiadoRepository.findAll();
    }

    /**
     * Obtiene colegiados habilitados
     */
    public List<Colegiado> obtenerColegiadosHabilitados() {
        return colegiadoRepository.findColegiadosHabilitados();
    }

    /**
     * Obtiene colegiados por estado de habilitación
     */
    public List<Colegiado> obtenerPorEstadoHabilitacion(EstadoHabilitacion estado) {
        return colegiadoRepository.findByEstadoHabilitacion(estado);
    }

    /**
     * Actualiza información de un colegiado
     */
    @Transactional
    public Colegiado actualizarColegiado(Long id, Colegiado colegiadoActualizado) {
        log.info("Actualizando colegiado ID: {}", id);

        Colegiado colegiado = obtenerColegiadoPorId(id);

        colegiado.setEspecialidad(colegiadoActualizado.getEspecialidad());
        colegiado.setUniversidad(colegiadoActualizado.getUniversidad());
        colegiado.setObservaciones(colegiadoActualizado.getObservaciones());

        return colegiadoRepository.save(colegiado);
    }

    /**
     * Obtiene colegiados con cuotas vencidas
     */
    public List<Colegiado> obtenerColegiadosConCuotasVencidas() {
        return colegiadoRepository.findColegiadosConCuotasVencidas();
    }
}
