<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador de autenticación
 */
class AuthController
{
    private $authService;

    public function __construct()
    {
        $this->authService = new AuthService();
    }

    /**
     * Muestra el formulario de login
     */
    public function showLogin()
    {
        // Si ya está autenticado, redirigir al dashboard
        if ($this->authService->isAuthenticated()) {
            header('Location: /dashboard.php');
            exit;
        }

        require_once VIEWS_PATH . '/auth/login.php';
    }

    /**
     * Procesa el login
     */
    public function login()
    {
        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            header('Location: /login.php');
            exit;
        }

        $username = $_POST['username'] ?? '';
        $password = $_POST['password'] ?? '';

        $result = $this->authService->login($username, $password);

        if ($result['success']) {
            header('Location: /dashboard.php');
            exit;
        }

        // Error de autenticación
        $_SESSION['error'] = $result['error'] ?? 'Credenciales inválidas';
        header('Location: /login.php');
        exit;
    }

    /**
     * Cierra la sesión
     */
    public function logout()
    {
        $this->authService->logout();
        header('Location: /login.php');
        exit;
    }
}
