<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador de autenticación
 */
class AuthController
{
    private $usuarioModel;

    public function __construct()
    {
        $this->usuarioModel = new Usuario();
    }

    /**
     * Muestra el formulario de login
     */
    public function showLogin()
    {
        // Si ya está autenticado, redirigir al dashboard
        if (isset($_SESSION['user_id'])) {
            redirect('dashboard.php');
        }

        require_once VIEWS_PATH . '/auth/login.php';
    }

    /**
     * Procesa el login
     */
    public function login()
    {
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            redirect('login.php');
        }

        $username = $_POST['username'] ?? '';
        $password = $_POST['password'] ?? '';

        $user = $this->usuarioModel->login($username, $password);

        if ($user) {
            // Guardar sesión
            $_SESSION['user_id'] = $user['id'];
            $_SESSION['username'] = $user['username'];
            $_SESSION['user_nombre'] = $user['nombres'] . ' ' . $user['apellidos'];
            $_SESSION['user_rol'] = $user['rol'];
            $_SESSION['login_time'] = time();

            redirect('dashboard.php');
        }

        // Error de autenticación
        $_SESSION['error'] = 'Credenciales inválidas';
        redirect('login.php');
    }

    /**
     * Cierra la sesión
     */
    public function logout()
    {
        session_destroy();
        redirect('login.php');
    }
}
