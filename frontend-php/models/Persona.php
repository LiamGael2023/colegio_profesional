<?php
/**
 * Modelo Persona
 */
class Persona
{
    private $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    /**
     * Busca una persona por DNI
     */
    public function buscarPorDni($dni)
    {
        $sql = "SELECT * FROM personas WHERE dni = ?";
        return $this->db->fetchOne($sql, [$dni]);
    }

    /**
     * Obtiene una persona por ID
     */
    public function obtenerPorId($id)
    {
        $sql = "SELECT * FROM personas WHERE id = ?";
        return $this->db->fetchOne($sql, [$id]);
    }

    /**
     * Busca personas por criterio (DNI o nombre)
     */
    public function buscar($criterio)
    {
        $sql = "SELECT * FROM personas
                WHERE dni LIKE ? OR
                      CONCAT(nombres, ' ', apellido_paterno, ' ', IFNULL(apellido_materno, '')) LIKE ?
                LIMIT 50";
        $param = "%{$criterio}%";
        return $this->db->fetchAll($sql, [$param, $param]);
    }

    /**
     * Crea una nueva persona
     */
    public function crear($data)
    {
        $sql = "INSERT INTO personas (dni, nombres, apellido_paterno, apellido_materno,
                                     email, telefono, direccion, fecha_nacimiento, tipo_persona)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        return $this->db->execute($sql, [
            $data['dni'],
            $data['nombres'],
            $data['apellido_paterno'],
            $data['apellido_materno'] ?? null,
            $data['email'] ?? null,
            $data['telefono'] ?? null,
            $data['direccion'] ?? null,
            $data['fecha_nacimiento'] ?? null,
            $data['tipo_persona'] ?? 'PUBLICO'
        ]);
    }

    /**
     * Actualiza una persona
     */
    public function actualizar($id, $data)
    {
        $sql = "UPDATE personas SET
                nombres = ?, apellido_paterno = ?, apellido_materno = ?,
                email = ?, telefono = ?, direccion = ?, fecha_nacimiento = ?
                WHERE id = ?";

        return $this->db->execute($sql, [
            $data['nombres'],
            $data['apellido_paterno'],
            $data['apellido_materno'] ?? null,
            $data['email'] ?? null,
            $data['telefono'] ?? null,
            $data['direccion'] ?? null,
            $data['fecha_nacimiento'] ?? null,
            $id
        ]);
    }

    /**
     * Obtiene todas las personas activas
     */
    public function obtenerActivas()
    {
        $sql = "SELECT * FROM personas WHERE activo = 1 ORDER BY nombres";
        return $this->db->fetchAll($sql);
    }

    /**
     * Desactiva una persona
     */
    public function desactivar($id)
    {
        $sql = "UPDATE personas SET activo = 0 WHERE id = ?";
        return $this->db->execute($sql, [$id]);
    }

    /**
     * Obtiene el nombre completo de una persona
     */
    public function obtenerNombreCompleto($persona)
    {
        $nombre = $persona['nombres'] . ' ' . $persona['apellido_paterno'];
        if (!empty($persona['apellido_materno'])) {
            $nombre .= ' ' . $persona['apellido_materno'];
        }
        return $nombre;
    }

    /**
     * Verifica si existe un DNI
     */
    public function existeDni($dni)
    {
        $sql = "SELECT COUNT(*) as total FROM personas WHERE dni = ?";
        $result = $this->db->fetchOne($sql, [$dni]);
        return $result['total'] > 0;
    }
}
