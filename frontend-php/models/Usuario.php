<?php
/**
 * Modelo Usuario
 */
class Usuario
{
    private $db;

    public function __construct()
    {
        $this->db = Database::getInstance();
    }

    /**
     * Autentica un usuario
     */
    public function login($username, $password)
    {
        $sql = "SELECT * FROM usuarios WHERE username = ? AND activo = 1";
        $user = $this->db->fetchOne($sql, [$username]);

        if ($user && password_verify($password, $user['password'])) {
            // Actualizar último acceso
            $this->actualizarUltimoAcceso($user['id']);
            return $user;
        }

        return false;
    }

    /**
     * Obtiene un usuario por ID
     */
    public function obtenerPorId($id)
    {
        $sql = "SELECT * FROM usuarios WHERE id = ?";
        return $this->db->fetchOne($sql, [$id]);
    }

    /**
     * Obtiene un usuario por username
     */
    public function obtenerPorUsername($username)
    {
        $sql = "SELECT * FROM usuarios WHERE username = ?";
        return $this->db->fetchOne($sql, [$username]);
    }

    /**
     * Crea un nuevo usuario
     */
    public function crear($data)
    {
        $sql = "INSERT INTO usuarios (username, password, email, nombres, apellidos, rol, activo)
                VALUES (?, ?, ?, ?, ?, ?, ?)";

        $hashedPassword = password_hash($data['password'], PASSWORD_BCRYPT);

        return $this->db->execute($sql, [
            $data['username'],
            $hashedPassword,
            $data['email'],
            $data['nombres'],
            $data['apellidos'],
            $data['rol'] ?? 'CAJERO',
            $data['activo'] ?? 1
        ]);
    }

    /**
     * Actualiza el último acceso
     */
    private function actualizarUltimoAcceso($userId)
    {
        $sql = "UPDATE usuarios SET ultimo_acceso = NOW() WHERE id = ?";
        $this->db->execute($sql, [$userId]);
    }

    /**
     * Obtiene todos los usuarios
     */
    public function obtenerTodos()
    {
        $sql = "SELECT id, username, email, nombres, apellidos, rol, activo, ultimo_acceso
                FROM usuarios ORDER BY id DESC";
        return $this->db->fetchAll($sql);
    }

    /**
     * Cambia la contraseña de un usuario
     */
    public function cambiarPassword($userId, $newPassword)
    {
        $sql = "UPDATE usuarios SET password = ? WHERE id = ?";
        $hashedPassword = password_hash($newPassword, PASSWORD_BCRYPT);
        return $this->db->execute($sql, [$hashedPassword, $userId]);
    }
}
