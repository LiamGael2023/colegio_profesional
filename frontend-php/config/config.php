<?php
/**
 * Configuración del sistema PHP MVC
 */

// Configuración de la base de datos MySQL
// Soporta variables de entorno (Docker) o valores por defecto (XAMPP)
define('DB_HOST', getenv('DB_HOST') ?: 'localhost');
define('DB_NAME', getenv('DB_NAME') ?: 'colegio_profesional');
define('DB_USER', getenv('DB_USER') ?: 'root');
define('DB_PASS', getenv('DB_PASS') ?: ''); // En XAMPP por defecto está vacío

// Configuración de la aplicación
define('APP_NAME', 'Colegio Profesional - Sistema de Gestión');
define('APP_VERSION', '1.0.0');
define('APP_TIMEZONE', 'America/Lima');
define('BASE_URL', 'http://localhost/colegio');

// Configuración de sesiones
define('SESSION_TIMEOUT', 86400); // 24 horas en segundos

// Configuración del sistema
define('CUOTA_MENSUAL_DEFAULT', 150.00);
define('MESES_TOLERANCIA_IMPAGO', 3);
define('DIA_GENERACION_CUOTAS', 1);
define('DIA_VENCIMIENTO_CUOTAS', 15);

// Configuración de rutas
define('BASE_PATH', __DIR__ . '/..');
define('VIEWS_PATH', BASE_PATH . '/views');
define('CONTROLLERS_PATH', BASE_PATH . '/controllers');
define('MODELS_PATH', BASE_PATH . '/models');
define('HELPERS_PATH', BASE_PATH . '/helpers');

// Timezone
date_default_timezone_set(APP_TIMEZONE);

// Iniciar sesión
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Autoloader
spl_autoload_register(function ($class) {
    $paths = [
        CONTROLLERS_PATH,
        MODELS_PATH,
        HELPERS_PATH
    ];

    foreach ($paths as $path) {
        $file = $path . '/' . $class . '.php';
        if (file_exists($file)) {
            require_once $file;
            return;
        }
    }
});

// Función helper para redireccionar
function redirect($url) {
    header('Location: ' . BASE_URL . '/' . $url);
    exit;
}

// Función helper para verificar autenticación
function requireAuth() {
    if (!isset($_SESSION['user_id'])) {
        redirect('login.php');
    }
}

// Función helper para verificar rol
function requireRole($role) {
    requireAuth();
    if ($_SESSION['user_rol'] !== $role) {
        die('Acceso denegado');
    }
}
