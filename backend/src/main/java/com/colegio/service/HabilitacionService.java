package com.colegio.service;

import com.colegio.model.entity.Colegiado;
import com.colegio.model.enums.EstadoAportacion;
import com.colegio.model.enums.EstadoHabilitacion;
import com.colegio.repository.AportacionRepository;
import com.colegio.repository.ColegiadoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Servicio para gestión de habilitación de colegiados
 * Incluye inhabilitación automática por meses impagos
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class HabilitacionService {

    private final ColegiadoRepository colegiadoRepository;
    private final AportacionRepository aportacionRepository;

    /**
     * Proceso automático de verificación de habilitación
     * Se ejecuta todos los días a las 02:00 AM
     * Cron: segundo minuto hora día mes día-semana
     */
    @Scheduled(cron = "0 0 2 * * ?")
    @Transactional
    public void verificarYActualizarHabilitaciones() {
        log.info("=== INICIANDO VERIFICACIÓN DE HABILITACIONES ===");

        try {
            actualizarContadorMesesImpagos();
            inhabilitarColegiadosPorImpagos();
            log.info("=== VERIFICACIÓN DE HABILITACIONES COMPLETADA ===");
        } catch (Exception e) {
            log.error("Error en verificación de habilitaciones: {}", e.getMessage(), e);
        }
    }

    /**
     * Actualiza el contador de meses impagos de todos los colegiados
     */
    @Transactional
    public void actualizarContadorMesesImpagos() {
        log.info("Actualizando contador de meses impagos...");

        List<Colegiado> colegiadosHabilitados = colegiadoRepository.findColegiadosHabilitados();
        int actualizados = 0;

        for (Colegiado colegiado : colegiadosHabilitados) {
            long mesesImpagos = aportacionRepository.countByPersonaIdAndEstado(
                colegiado.getPersona().getId(),
                EstadoAportacion.PENDIENTE
            );

            if (colegiado.getMesesImpagosConsecutivos() != mesesImpagos) {
                colegiado.setMesesImpagosConsecutivos((int) mesesImpagos);
                colegiadoRepository.save(colegiado);
                actualizados++;

                log.debug("Colegiado {}: {} meses impagos",
                    colegiado.getNumeroColegiatura(), mesesImpagos);
            }
        }

        log.info("Contadores actualizados: {}", actualizados);
    }

    /**
     * Inhabilita automáticamente los colegiados que exceden el límite de meses impagos
     */
    @Transactional
    public int inhabilitarColegiadosPorImpagos() {
        log.info("Verificando colegiados para inhabilitar...");

        List<Colegiado> colegiadosParaInhabilitar = colegiadoRepository.findColegiadosParaInhabilitar();
        int inhabilitados = 0;

        for (Colegiado colegiado : colegiadosParaInhabilitar) {
            colegiado.setEstadoHabilitacion(EstadoHabilitacion.INHABILITADO);
            colegiado.setObservaciones(
                String.format("Inhabilitado automáticamente por %d meses impagos (tolerancia: %d)",
                    colegiado.getMesesImpagosConsecutivos(),
                    colegiado.getMesesToleranciaImpago())
            );
            colegiadoRepository.save(colegiado);
            inhabilitados++;

            log.warn("Colegiado INHABILITADO: {} - {} ({} meses impagos)",
                colegiado.getNumeroColegiatura(),
                colegiado.getPersona().getNombreCompleto(),
                colegiado.getMesesImpagosConsecutivos());
        }

        log.info("Colegiados inhabilitados: {}", inhabilitados);
        return inhabilitados;
    }

    /**
     * Habilita manualmente un colegiado
     */
    @Transactional
    public void habilitarColegiado(Long colegiadoId, String observaciones) {
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
            .orElseThrow(() -> new RuntimeException("Colegiado no encontrado"));

        colegiado.setEstadoHabilitacion(EstadoHabilitacion.HABILITADO);
        colegiado.setObservaciones(observaciones);
        colegiadoRepository.save(colegiado);

        log.info("Colegiado habilitado manualmente: {}", colegiado.getNumeroColegiatura());
    }

    /**
     * Inhabilita manualmente un colegiado
     */
    @Transactional
    public void inhabilitarColegiado(Long colegiadoId, String observaciones) {
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
            .orElseThrow(() -> new RuntimeException("Colegiado no encontrado"));

        colegiado.setEstadoHabilitacion(EstadoHabilitacion.INHABILITADO);
        colegiado.setObservaciones(observaciones);
        colegiadoRepository.save(colegiado);

        log.info("Colegiado inhabilitado manualmente: {}", colegiado.getNumeroColegiatura());
    }

    /**
     * Suspende un colegiado
     */
    @Transactional
    public void suspenderColegiado(Long colegiadoId, String observaciones) {
        Colegiado colegiado = colegiadoRepository.findById(colegiadoId)
            .orElseThrow(() -> new RuntimeException("Colegiado no encontrado"));

        colegiado.setEstadoHabilitacion(EstadoHabilitacion.SUSPENDIDO);
        colegiado.setObservaciones(observaciones);
        colegiadoRepository.save(colegiado);

        log.info("Colegiado suspendido: {}", colegiado.getNumeroColegiatura());
    }

    /**
     * Verifica si un colegiado está habilitado
     */
    public boolean estaColegiadoHabilitado(Long colegiadoId) {
        return colegiadoRepository.findById(colegiadoId)
            .map(Colegiado::isHabilitado)
            .orElse(false);
    }

    /**
     * Obtiene el estado de habilitación de un colegiado
     */
    public EstadoHabilitacion obtenerEstadoHabilitacion(Long colegiadoId) {
        return colegiadoRepository.findById(colegiadoId)
            .map(Colegiado::getEstadoHabilitacion)
            .orElseThrow(() -> new RuntimeException("Colegiado no encontrado"));
    }
}
