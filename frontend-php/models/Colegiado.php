<?php
/**
 * Modelo Colegiado
 */
class Colegiado
{
    private $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    /**
     * Convierte una persona a colegiado
     */
    public function convertir($data)
    {
        try {
            $this->db->beginTransaction();

            // Insertar en colegiados
            $sql = "INSERT INTO colegiados (persona_id, numero_colegiatura, especialidad,
                                          universidad, fecha_colegiatura, estado_habilitacion,
                                          meses_tolerancia_impago, meses_impagos_consecutivos)
                    VALUES (?, ?, ?, ?, ?, 'HABILITADO', ?, 0)";

            $this->db->execute($sql, [
                $data['persona_id'],
                $data['numero_colegiatura'],
                $data['especialidad'] ?? null,
                $data['universidad'] ?? null,
                $data['fecha_colegiatura'] ?? date('Y-m-d'),
                MESES_TOLERANCIA_IMPAGO
            ]);

            // Actualizar tipo_persona
            $sql2 = "UPDATE personas SET tipo_persona = 'COLEGIADO' WHERE id = ?";
            $this->db->execute($sql2, [$data['persona_id']]);

            $this->db->commit();
            return true;
        } catch (Exception $e) {
            $this->db->rollBack();
            return false;
        }
    }

    /**
     * Obtiene un colegiado por ID de persona
     */
    public function obtenerPorPersonaId($personaId)
    {
        $sql = "SELECT c.*, p.*,
                c.id as colegiado_id,
                p.id as persona_id
                FROM colegiados c
                INNER JOIN personas p ON c.persona_id = p.id
                WHERE c.persona_id = ?";
        return $this->db->fetchOne($sql, [$personaId]);
    }

    /**
     * Obtiene un colegiado por número de colegiatura
     */
    public function obtenerPorNumeroColegiatura($numero)
    {
        $sql = "SELECT c.*, p.*,
                c.id as colegiado_id,
                p.id as persona_id
                FROM colegiados c
                INNER JOIN personas p ON c.persona_id = p.id
                WHERE c.numero_colegiatura = ?";
        return $this->db->fetchOne($sql, [$numero]);
    }

    /**
     * Obtiene todos los colegiados
     */
    public function obtenerTodos()
    {
        $sql = "SELECT c.*, p.*,
                c.id as colegiado_id,
                p.id as persona_id,
                CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', IFNULL(p.apellido_materno, '')) as nombre_completo
                FROM colegiados c
                INNER JOIN personas p ON c.persona_id = p.id
                WHERE p.activo = 1
                ORDER BY c.numero_colegiatura";
        return $this->db->fetchAll($sql);
    }

    /**
     * Obtiene colegiados habilitados
     */
    public function obtenerHabilitados()
    {
        $sql = "SELECT c.*, p.*,
                c.id as colegiado_id,
                p.id as persona_id
                FROM colegiados c
                INNER JOIN personas p ON c.persona_id = p.id
                WHERE c.estado_habilitacion = 'HABILITADO' AND p.activo = 1
                ORDER BY c.numero_colegiatura";
        return $this->db->fetchAll($sql);
    }

    /**
     * Habilita un colegiado
     */
    public function habilitar($colegiadoId, $observaciones = '')
    {
        $sql = "UPDATE colegiados SET
                estado_habilitacion = 'HABILITADO',
                observaciones = ?
                WHERE id = ?";
        return $this->db->execute($sql, [$observaciones, $colegiadoId]);
    }

    /**
     * Inhabilita un colegiado
     */
    public function inhabilitar($colegiadoId, $observaciones = '')
    {
        $sql = "UPDATE colegiados SET
                estado_habilitacion = 'INHABILITADO',
                observaciones = ?
                WHERE id = ?";
        return $this->db->execute($sql, [$observaciones, $colegiadoId]);
    }

    /**
     * Actualiza el contador de meses impagos
     */
    public function actualizarMesesImpagos($colegiadoId, $meses)
    {
        $sql = "UPDATE colegiados SET meses_impagos_consecutivos = ? WHERE id = ?";
        return $this->db->execute($sql, [$meses, $colegiadoId]);
    }

    /**
     * Verifica si existe el número de colegiatura
     */
    public function existeNumeroColegiatura($numero)
    {
        $sql = "SELECT COUNT(*) as total FROM colegiados WHERE numero_colegiatura = ?";
        $result = $this->db->fetchOne($sql, [$numero]);
        return $result['total'] > 0;
    }

    /**
     * Obtiene estadísticas de colegiados
     */
    public function obtenerEstadisticas()
    {
        $sql = "SELECT
                COUNT(*) as total,
                SUM(CASE WHEN estado_habilitacion = 'HABILITADO' THEN 1 ELSE 0 END) as habilitados,
                SUM(CASE WHEN estado_habilitacion = 'INHABILITADO' THEN 1 ELSE 0 END) as inhabilitados,
                SUM(CASE WHEN estado_habilitacion = 'SUSPENDIDO' THEN 1 ELSE 0 END) as suspendidos
                FROM colegiados c
                INNER JOIN personas p ON c.persona_id = p.id
                WHERE p.activo = 1";
        return $this->db->fetchOne($sql);
    }
}
