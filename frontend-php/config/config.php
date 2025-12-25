<?php
/**
 * Configuración del sistema Frontend PHP
 */

// Configuración de la API REST (Spring Boot)
define('API_BASE_URL', 'http://localhost:8080/api');
define('API_AUTH_URL', 'http://localhost:8080/api/auth');

// Configuración de la aplicación
define('APP_NAME', 'Colegio Profesional - Administración');
define('APP_VERSION', '1.0.0');
define('APP_TIMEZONE', 'America/Lima');

// Configuración de sesiones
define('SESSION_TIMEOUT', 86400); // 24 horas en segundos

// Configuración de rutas
define('BASE_PATH', __DIR__ . '/..');
define('VIEWS_PATH', BASE_PATH . '/views');
define('CONTROLLERS_PATH', BASE_PATH . '/controllers');
define('SERVICES_PATH', BASE_PATH . '/services');
define('MODELS_PATH', BASE_PATH . '/models');

// Timezone
date_default_timezone_set(APP_TIMEZONE);

// Iniciar sesión
if (session_status() === PHP_SESSION_NONE) {
    session_start();
}

// Autoloader simple
spl_autoload_register(function ($class) {
    $paths = [
        CONTROLLERS_PATH,
        SERVICES_PATH,
        MODELS_PATH
    ];

    foreach ($paths as $path) {
        $file = $path . '/' . $class . '.php';
        if (file_exists($file)) {
            require_once $file;
            return;
        }
    }
});
