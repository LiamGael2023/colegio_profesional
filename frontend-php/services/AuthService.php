<?php
/**
 * Servicio de autenticación
 * Consume el endpoint /auth del backend
 */
class AuthService
{
    private $authUrl;

    public function __construct()
    {
        $this->authUrl = API_AUTH_URL;
    }

    /**
     * Inicia sesión con username y password
     */
    public function login($username, $password)
    {
        $ch = curl_init();

        $data = [
            'username' => $username,
            'password' => $password
        ];

        curl_setopt($ch, CURLOPT_URL, $this->authUrl . '/login');
        curl_setopt($ch, CURLOPT_POST, true);
        curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_HTTPHEADER, [
            'Content-Type: application/json',
            'Accept: application/json'
        ]);

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);

        curl_close($ch);

        if ($error) {
            return [
                'success' => false,
                'error' => 'Error de conexión: ' . $error
            ];
        }

        $result = json_decode($response, true);

        if ($httpCode === 200 && isset($result['token'])) {
            // Guardar token y datos de usuario en sesión
            $_SESSION['auth_token'] = $result['token'];
            $_SESSION['user'] = [
                'username' => $result['username'],
                'nombreCompleto' => $result['nombreCompleto'],
                'rol' => $result['rol']
            ];
            $_SESSION['login_time'] = time();

            return [
                'success' => true,
                'data' => $result
            ];
        }

        return [
            'success' => false,
            'error' => $result['error'] ?? 'Credenciales inválidas'
        ];
    }

    /**
     * Cierra la sesión
     */
    public function logout()
    {
        session_destroy();
        return true;
    }

    /**
     * Verifica si el usuario está autenticado
     */
    public function isAuthenticated()
    {
        if (!isset($_SESSION['auth_token'])) {
            return false;
        }

        // Verificar timeout de sesión
        if (isset($_SESSION['login_time'])) {
            $elapsed = time() - $_SESSION['login_time'];
            if ($elapsed > SESSION_TIMEOUT) {
                $this->logout();
                return false;
            }
        }

        return true;
    }

    /**
     * Obtiene el usuario actual
     */
    public function getCurrentUser()
    {
        return $_SESSION['user'] ?? null;
    }

    /**
     * Obtiene el token actual
     */
    public function getToken()
    {
        return $_SESSION['auth_token'] ?? null;
    }

    /**
     * Verifica si el usuario tiene un rol específico
     */
    public function hasRole($role)
    {
        $user = $this->getCurrentUser();
        return $user && $user['rol'] === $role;
    }
}
