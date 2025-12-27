<?php
/**
 * Modelo Aportacion
 */
class Aportacion
{
    private $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    /**
     * Obtiene aportaciones pendientes de una persona
     */
    public function obtenerPendientes($personaId)
    {
        $sql = "SELECT * FROM aportaciones
                WHERE persona_id = ? AND estado = 'PENDIENTE'
                ORDER BY anio ASC, mes ASC";
        return $this->db->fetchAll($sql, [$personaId]);
    }

    /**
     * Obtiene todas las aportaciones de una persona
     */
    public function obtenerPorPersona($personaId)
    {
        $sql = "SELECT * FROM aportaciones
                WHERE persona_id = ?
                ORDER BY anio DESC, mes DESC";
        return $this->db->fetchAll($sql, [$personaId]);
    }

    /**
     * Obtiene el total de deuda pendiente
     */
    public function obtenerTotalDeuda($personaId)
    {
        $sql = "SELECT COALESCE(SUM(monto), 0) as total
                FROM aportaciones
                WHERE persona_id = ? AND estado = 'PENDIENTE'";
        $result = $this->db->fetchOne($sql, [$personaId]);
        return $result['total'];
    }

    /**
     * Crea una aportación manual
     */
    public function crearManual($data)
    {
        // Verificar que no exista duplicado
        if ($this->existe($data['persona_id'], $data['mes'], $data['anio'])) {
            return false;
        }

        $sql = "INSERT INTO aportaciones (persona_id, mes, anio, monto, concepto,
                                         estado, fecha_vencimiento, tipo_generacion)
                VALUES (?, ?, ?, ?, ?, 'PENDIENTE', ?, 'MANUAL')";

        $fechaVencimiento = $this->calcularFechaVencimiento($data['mes'], $data['anio']);

        return $this->db->execute($sql, [
            $data['persona_id'],
            $data['mes'],
            $data['anio'],
            $data['monto'] ?? CUOTA_MENSUAL_DEFAULT,
            $data['concepto'] ?? "Cuota mensual {$data['mes']}/{$data['anio']}",
            $fechaVencimiento
        ]);
    }

    /**
     * Genera cuotas para un mes y año
     */
    public function generarCuotasMensuales($mes, $anio)
    {
        $colegiadoModel = new Colegiado();
        $habilitados = $colegiadoModel->obtenerHabilitados();

        $generadas = 0;
        $fechaVencimiento = $this->calcularFechaVencimiento($mes, $anio);

        foreach ($habilitados as $colegiado) {
            // Verificar anti-duplicados
            if ($this->existe($colegiado['persona_id'], $mes, $anio)) {
                continue;
            }

            $sql = "INSERT INTO aportaciones (persona_id, mes, anio, monto, concepto,
                                             estado, fecha_vencimiento, tipo_generacion)
                    VALUES (?, ?, ?, ?, ?, 'PENDIENTE', ?, 'AUTOMATICA')";

            $this->db->execute($sql, [
                $colegiado['persona_id'],
                $mes,
                $anio,
                CUOTA_MENSUAL_DEFAULT,
                "Cuota mensual de colegiatura - {$mes}/{$anio}",
                $fechaVencimiento
            ]);

            $generadas++;
        }

        return $generadas;
    }

    /**
     * Marca una aportación como pagada
     */
    public function marcarComoPagada($aportacionId)
    {
        $sql = "UPDATE aportaciones SET estado = 'PAGADO' WHERE id = ?";
        return $this->db->execute($sql, [$aportacionId]);
    }

    /**
     * Anula una aportación
     */
    public function anular($aportacionId)
    {
        $sql = "UPDATE aportaciones SET estado = 'ANULADO' WHERE id = ?";
        return $this->db->execute($sql, [$aportacionId]);
    }

    /**
     * Verifica si existe una aportación
     */
    public function existe($personaId, $mes, $anio)
    {
        $sql = "SELECT COUNT(*) as total FROM aportaciones
                WHERE persona_id = ? AND mes = ? AND anio = ?";
        $result = $this->db->fetchOne($sql, [$personaId, $mes, $anio]);
        return $result['total'] > 0;
    }

    /**
     * Obtiene aportaciones por IDs
     */
    public function obtenerPorIds($ids)
    {
        if (empty($ids)) return [];

        $placeholders = implode(',', array_fill(0, count($ids), '?'));
        $sql = "SELECT * FROM aportaciones WHERE id IN ($placeholders)";
        return $this->db->fetchAll($sql, $ids);
    }

    /**
     * Cuenta aportaciones pendientes de una persona
     */
    public function contarPendientes($personaId)
    {
        $sql = "SELECT COUNT(*) as total FROM aportaciones
                WHERE persona_id = ? AND estado = 'PENDIENTE'";
        $result = $this->db->fetchOne($sql, [$personaId]);
        return $result['total'];
    }

    /**
     * Calcula la fecha de vencimiento
     */
    private function calcularFechaVencimiento($mes, $anio)
    {
        $dia = DIA_VENCIMIENTO_CUOTAS;
        $ultimoDia = date('t', strtotime("{$anio}-{$mes}-01"));

        if ($dia > $ultimoDia) {
            $dia = $ultimoDia;
        }

        return "{$anio}-{$mes}-{$dia}";
    }

    /**
     * Obtiene estadísticas de aportaciones
     */
    public function obtenerEstadisticas($mes = null, $anio = null)
    {
        $sql = "SELECT
                COUNT(*) as total,
                SUM(CASE WHEN estado = 'PENDIENTE' THEN 1 ELSE 0 END) as pendientes,
                SUM(CASE WHEN estado = 'PAGADO' THEN 1 ELSE 0 END) as pagadas,
                SUM(CASE WHEN estado = 'PENDIENTE' THEN monto ELSE 0 END) as monto_pendiente,
                SUM(CASE WHEN estado = 'PAGADO' THEN monto ELSE 0 END) as monto_pagado
                FROM aportaciones";

        $params = [];
        if ($mes && $anio) {
            $sql .= " WHERE mes = ? AND anio = ?";
            $params = [$mes, $anio];
        }

        return $this->db->fetchOne($sql, $params);
    }
}
