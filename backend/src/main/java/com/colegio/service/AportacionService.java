package com.colegio.service;

import com.colegio.model.entity.Aportacion;
import com.colegio.model.entity.Colegiado;
import com.colegio.model.entity.Configuracion;
import com.colegio.model.entity.Persona;
import com.colegio.model.enums.EstadoAportacion;
import com.colegio.model.enums.TipoGeneracion;
import com.colegio.repository.AportacionRepository;
import com.colegio.repository.ColegiadoRepository;
import com.colegio.repository.ConfiguracionRepository;
import com.colegio.repository.PersonaRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.List;

/**
 * Servicio para gestión de aportaciones
 * Incluye generación automática de cuotas mensuales
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AportacionService {

    private final AportacionRepository aportacionRepository;
    private final ColegiadoRepository colegiadoRepository;
    private final PersonaRepository personaRepository;
    private final ConfiguracionRepository configuracionRepository;

    /**
     * Generación automática de cuotas mensuales
     * Se ejecuta el día 1 de cada mes a las 00:01
     * Cron: segundo minuto hora día mes día-semana
     */
    @Scheduled(cron = "0 1 0 1 * ?")
    @Transactional
    public void generarCuotasMensualesAutomaticas() {
        log.info("=== INICIANDO GENERACIÓN AUTOMÁTICA DE CUOTAS MENSUALES ===");

        LocalDate hoy = LocalDate.now();
        int mesActual = hoy.getMonthValue();
        int anioActual = hoy.getYear();

        log.info("Generando cuotas para: {}/{}", mesActual, anioActual);

        try {
            int cuotasGeneradas = generarCuotasParaMesAnio(mesActual, anioActual, TipoGeneracion.AUTOMATICA);
            log.info("=== GENERACIÓN COMPLETADA: {} cuotas generadas ===", cuotasGeneradas);
        } catch (Exception e) {
            log.error("Error en generación automática de cuotas: {}", e.getMessage(), e);
        }
    }

    /**
     * Genera cuotas para un mes y año específico
     * Solo para colegiados HABILITADOS
     * Aplica regla anti-duplicados
     */
    @Transactional
    public int generarCuotasParaMesAnio(Integer mes, Integer anio, TipoGeneracion tipoGeneracion) {
        log.info("Generando cuotas para mes: {}, año: {}, tipo: {}", mes, anio, tipoGeneracion);

        // Obtener configuraciones
        BigDecimal montoCuota = obtenerMontoCuotaDefault();
        int diaVencimiento = obtenerDiaVencimiento();

        // Obtener colegiados habilitados
        List<Colegiado> colegiadosHabilitados = colegiadoRepository.findColegiadosHabilitados();
        log.info("Colegiados habilitados encontrados: {}", colegiadosHabilitados.size());

        int cuotasGeneradas = 0;
        int cuotasOmitidas = 0;

        for (Colegiado colegiado : colegiadosHabilitados) {
            Long personaId = colegiado.getPersona().getId();

            // REGLA ANTI-DUPLICADOS: Verificar si ya existe la cuota
            if (aportacionRepository.existsByPersonaIdAndMesAndAnio(personaId, mes, anio)) {
                cuotasOmitidas++;
                log.debug("Cuota ya existe para persona ID: {}, mes: {}, año: {}", personaId, mes, anio);
                continue;
            }

            // Crear la aportación
            Aportacion aportacion = Aportacion.builder()
                .persona(colegiado.getPersona())
                .mes(mes)
                .anio(anio)
                .monto(montoCuota)
                .concepto("Cuota mensual de colegiatura - " + mes + "/" + anio)
                .estado(EstadoAportacion.PENDIENTE)
                .fechaVencimiento(calcularFechaVencimiento(mes, anio, diaVencimiento))
                .tipoGeneracion(tipoGeneracion)
                .build();

            aportacionRepository.save(aportacion);
            cuotasGeneradas++;

            log.debug("Cuota generada para: {} - {} {}",
                colegiado.getNumeroColegiatura(),
                colegiado.getPersona().getNombreCompleto(),
                montoCuota);
        }

        log.info("Resultado: {} cuotas generadas, {} omitidas (duplicadas)", cuotasGeneradas, cuotasOmitidas);
        return cuotasGeneradas;
    }

    /**
     * Crea aportación manual (pagos adelantados)
     * Permite crear obligaciones para meses futuros
     */
    @Transactional
    public Aportacion crearAportacionManual(Long personaId, Integer mes, Integer anio, BigDecimal monto, String concepto) {
        log.info("Creando aportación manual para persona ID: {}, mes: {}, año: {}", personaId, mes, anio);

        // Verificar que la persona existe
        Persona persona = personaRepository.findById(personaId)
            .orElseThrow(() -> new RuntimeException("Persona no encontrada con ID: " + personaId));

        // REGLA ANTI-DUPLICADOS
        if (aportacionRepository.existsByPersonaIdAndMesAndAnio(personaId, mes, anio)) {
            throw new RuntimeException("Ya existe una aportación para esta persona en " + mes + "/" + anio);
        }

        // Usar monto por defecto si no se especifica
        if (monto == null) {
            monto = obtenerMontoCuotaDefault();
        }

        // Usar concepto por defecto si no se especifica
        if (concepto == null || concepto.isEmpty()) {
            concepto = "Cuota mensual de colegiatura - " + mes + "/" + anio;
        }

        int diaVencimiento = obtenerDiaVencimiento();

        Aportacion aportacion = Aportacion.builder()
            .persona(persona)
            .mes(mes)
            .anio(anio)
            .monto(monto)
            .concepto(concepto)
            .estado(EstadoAportacion.PENDIENTE)
            .fechaVencimiento(calcularFechaVencimiento(mes, anio, diaVencimiento))
            .tipoGeneracion(TipoGeneracion.MANUAL)
            .build();

        return aportacionRepository.save(aportacion);
    }

    /**
     * Crea múltiples aportaciones manuales (pagos adelantados de varios meses)
     */
    @Transactional
    public List<Aportacion> crearAportacionesAdelantadas(Long personaId, Integer mesInicio, Integer anioInicio, Integer cantidadMeses) {
        log.info("Creando {} aportaciones adelantadas desde {}/{} para persona ID: {}",
            cantidadMeses, mesInicio, anioInicio, personaId);

        List<Aportacion> aportaciones = new ArrayList<>();
        YearMonth fechaInicio = YearMonth.of(anioInicio, mesInicio);

        for (int i = 0; i < cantidadMeses; i++) {
            YearMonth fecha = fechaInicio.plusMonths(i);
            try {
                Aportacion aportacion = crearAportacionManual(
                    personaId,
                    fecha.getMonthValue(),
                    fecha.getYear(),
                    null,
                    null
                );
                aportaciones.add(aportacion);
            } catch (Exception e) {
                log.warn("No se pudo crear aportación para {}/{}: {}",
                    fecha.getMonthValue(), fecha.getYear(), e.getMessage());
            }
        }

        return aportaciones;
    }

    /**
     * Obtiene aportaciones pendientes de una persona
     */
    public List<Aportacion> obtenerAportacionesPendientes(Long personaId) {
        return aportacionRepository.findAportacionesPendientesPorPersona(personaId);
    }

    /**
     * Obtiene el total de deuda pendiente de una persona
     */
    public BigDecimal obtenerTotalDeuda(Long personaId) {
        return aportacionRepository.getTotalDeudaPendiente(personaId);
    }

    /**
     * Obtiene aportaciones de una persona
     */
    public List<Aportacion> obtenerAportacionesPorPersona(Long personaId) {
        return aportacionRepository.findByPersonaIdOrderByAnioDescMesDesc(personaId);
    }

    /**
     * Anula una aportación
     */
    @Transactional
    public void anularAportacion(Long aportacionId) {
        Aportacion aportacion = aportacionRepository.findById(aportacionId)
            .orElseThrow(() -> new RuntimeException("Aportación no encontrada"));

        if (aportacion.isPagada()) {
            throw new RuntimeException("No se puede anular una aportación pagada");
        }

        aportacion.anular();
        aportacionRepository.save(aportacion);
        log.info("Aportación ID: {} anulada", aportacionId);
    }

    // ========== MÉTODOS AUXILIARES ==========

    /**
     * Obtiene el monto de cuota mensual por defecto desde configuración
     */
    private BigDecimal obtenerMontoCuotaDefault() {
        return configuracionRepository.findByClave("CUOTA_MENSUAL_DEFAULT")
            .map(config -> new BigDecimal(config.getValor()))
            .orElse(new BigDecimal("150.00"));
    }

    /**
     * Obtiene el día de vencimiento desde configuración
     */
    private int obtenerDiaVencimiento() {
        return configuracionRepository.findByClave("DIA_VENCIMIENTO_CUOTAS")
            .map(config -> Integer.parseInt(config.getValor()))
            .orElse(15);
    }

    /**
     * Calcula la fecha de vencimiento
     */
    private LocalDate calcularFechaVencimiento(Integer mes, Integer anio, int diaVencimiento) {
        YearMonth yearMonth = YearMonth.of(anio, mes);
        int ultimoDiaDelMes = yearMonth.lengthOfMonth();

        // Si el día de vencimiento es mayor que los días del mes, usar el último día
        int dia = Math.min(diaVencimiento, ultimoDiaDelMes);

        return LocalDate.of(anio, mes, dia);
    }
}
