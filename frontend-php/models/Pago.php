<?php
/**
 * Modelo Pago
 */
class Pago
{
    private $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    /**
     * Procesa un pago para múltiples aportaciones
     */
    public function procesar($data)
    {
        try {
            $this->db->beginTransaction();

            // Calcular monto total
            $aportacionModel = new Aportacion();
            $aportaciones = $aportacionModel->obtenerPorIds($data['aportaciones_ids']);

            if (empty($aportaciones)) {
                throw new Exception("No se encontraron aportaciones");
            }

            $montoTotal = array_sum(array_column($aportaciones, 'monto'));

            // Insertar pago
            $sql = "INSERT INTO pagos (persona_id, monto_total, metodo_pago, numero_comprobante,
                                      tipo_comprobante, observaciones, fecha_pago, usuario_registro)
                    VALUES (?, ?, ?, ?, ?, ?, NOW(), ?)";

            $this->db->execute($sql, [
                $data['persona_id'],
                $montoTotal,
                $data['metodo_pago'],
                $data['numero_comprobante'] ?? null,
                $data['tipo_comprobante'] ?? 'RECIBO',
                $data['observaciones'] ?? null,
                $data['usuario_registro'] ?? $_SESSION['username'] ?? 'SISTEMA'
            ]);

            $pagoId = $this->db->lastInsertId();

            // Insertar detalles y marcar aportaciones como pagadas
            foreach ($aportaciones as $aportacion) {
                // Detalle del pago
                $sql2 = "INSERT INTO detalle_pagos (pago_id, aportacion_id, monto_aplicado)
                        VALUES (?, ?, ?)";
                $this->db->execute($sql2, [$pagoId, $aportacion['id'], $aportacion['monto']]);

                // Marcar aportación como pagada
                $aportacionModel->marcarComoPagada($aportacion['id']);
            }

            // Actualizar estado de habilitación del colegiado
            $this->actualizarEstadoHabilitacion($data['persona_id']);

            $this->db->commit();

            return [
                'success' => true,
                'pago_id' => $pagoId,
                'monto_total' => $montoTotal
            ];
        } catch (Exception $e) {
            $this->db->rollBack();
            return [
                'success' => false,
                'error' => $e->getMessage()
            ];
        }
    }

    /**
     * Obtiene el historial de pagos de una persona
     */
    public function obtenerHistorial($personaId)
    {
        $sql = "SELECT * FROM pagos
                WHERE persona_id = ?
                ORDER BY fecha_pago DESC";
        return $this->db->fetchAll($sql, [$personaId]);
    }

    /**
     * Obtiene un pago por ID
     */
    public function obtenerPorId($id)
    {
        $sql = "SELECT * FROM pagos WHERE id = ?";
        return $this->db->fetchOne($sql, [$id]);
    }

    /**
     * Obtiene los detalles de un pago
     */
    public function obtenerDetalles($pagoId)
    {
        $sql = "SELECT dp.*, a.mes, a.anio, a.concepto
                FROM detalle_pagos dp
                INNER JOIN aportaciones a ON dp.aportacion_id = a.id
                WHERE dp.pago_id = ?
                ORDER BY a.anio, a.mes";
        return $this->db->fetchAll($sql, [$pagoId]);
    }

    /**
     * Obtiene el total pagado por una persona
     */
    public function obtenerTotalPagado($personaId)
    {
        $sql = "SELECT COALESCE(SUM(monto_total), 0) as total
                FROM pagos
                WHERE persona_id = ?";
        $result = $this->db->fetchOne($sql, [$personaId]);
        return $result['total'];
    }

    /**
     * Obtiene los pagos del día
     */
    public function obtenerPagosDelDia()
    {
        $sql = "SELECT p.*, per.dni,
                CONCAT(per.nombres, ' ', per.apellido_paterno) as nombre_completo
                FROM pagos p
                INNER JOIN personas per ON p.persona_id = per.id
                WHERE DATE(p.fecha_pago) = CURDATE()
                ORDER BY p.fecha_pago DESC";
        return $this->db->fetchAll($sql);
    }

    /**
     * Obtiene el total recaudado en un período
     */
    public function obtenerTotalRecaudado($fechaInicio, $fechaFin)
    {
        $sql = "SELECT COALESCE(SUM(monto_total), 0) as total
                FROM pagos
                WHERE fecha_pago BETWEEN ? AND ?";
        $result = $this->db->fetchOne($sql, [$fechaInicio, $fechaFin]);
        return $result['total'];
    }

    /**
     * Obtiene estadísticas de pagos por método
     */
    public function obtenerEstadisticasPorMetodo($fechaInicio, $fechaFin)
    {
        $sql = "SELECT metodo_pago, COUNT(*) as cantidad, SUM(monto_total) as total
                FROM pagos
                WHERE fecha_pago BETWEEN ? AND ?
                GROUP BY metodo_pago";
        return $this->db->fetchAll($sql, [$fechaInicio, $fechaFin]);
    }

    /**
     * Actualiza el estado de habilitación después de un pago
     */
    private function actualizarEstadoHabilitacion($personaId)
    {
        $colegiadoModel = new Colegiado();
        $colegiado = $colegiadoModel->obtenerPorPersonaId($personaId);

        if (!$colegiado) {
            return; // No es colegiado
        }

        // Contar cuántas aportaciones pendientes quedan
        $aportacionModel = new Aportacion();
        $pendientes = $aportacionModel->contarPendientes($personaId);

        // Actualizar contador de meses impagos
        $colegiadoModel->actualizarMesesImpagos($colegiado['colegiado_id'], $pendientes);

        // Si no hay pendientes, reiniciar y habilitar
        if ($pendientes == 0) {
            $colegiadoModel->habilitar($colegiado['colegiado_id'], 'Habilitado automáticamente - Todas las deudas pagadas');
        }
    }

    /**
     * Obtiene estadísticas generales de pagos
     */
    public function obtenerEstadisticas($mes = null, $anio = null)
    {
        $sql = "SELECT
                COUNT(*) as total_pagos,
                SUM(monto_total) as total_recaudado,
                AVG(monto_total) as promedio_pago,
                MIN(monto_total) as pago_minimo,
                MAX(monto_total) as pago_maximo
                FROM pagos";

        $params = [];
        if ($mes && $anio) {
            $sql .= " WHERE MONTH(fecha_pago) = ? AND YEAR(fecha_pago) = ?";
            $params = [$mes, $anio];
        }

        return $this->db->fetchOne($sql, $params);
    }

    /**
     * Obtiene el estado de cuenta de una persona
     */
    public function obtenerEstadoCuenta($personaId)
    {
        $sql = "SELECT
                a.mes, a.anio, a.monto, a.concepto, a.estado, a.fecha_vencimiento,
                p.fecha_pago, p.numero_comprobante, p.metodo_pago
                FROM aportaciones a
                LEFT JOIN detalle_pagos dp ON a.id = dp.aportacion_id
                LEFT JOIN pagos p ON dp.pago_id = p.id
                WHERE a.persona_id = ?
                ORDER BY a.anio DESC, a.mes DESC";
        return $this->db->fetchAll($sql, [$personaId]);
    }
}
