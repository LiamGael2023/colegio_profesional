<?php
require_once __DIR__ . '/config/config.php';

$authService = new AuthService();

// Verificar autenticación
if (!$authService->isAuthenticated()) {
    header('Location: /login.php');
    exit;
}

$user = $authService->getCurrentUser();
$pageTitle = 'Dashboard';

require_once VIEWS_PATH . '/layout/header.php';
?>

<div class="row">
    <div class="col-md-12">
        <div class="alert alert-success">
            <h4>¡Bienvenido al Sistema de Gestión de Colegio Profesional!</h4>
            <p>Sesión iniciada como: <strong><?= htmlspecialchars($user['nombreCompleto']) ?></strong></p>
            <p>Rol: <span class="badge bg-info"><?= htmlspecialchars($user['rol']) ?></span></p>
        </div>
    </div>
</div>

<div class="row">
    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <i class="fas fa-search fa-3x text-primary mb-3"></i>
                <h5>Buscar Persona</h5>
                <p>Busca personas por DNI</p>
                <a href="buscador.php" class="btn btn-primary">Ir al Buscador</a>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <i class="fas fa-cash-register fa-3x text-success mb-3"></i>
                <h5>Módulo de Caja</h5>
                <p>Procesar pagos y obligaciones</p>
                <a href="caja.php" class="btn btn-success">Ir a Caja</a>
            </div>
        </div>
    </div>

    <div class="col-md-4">
        <div class="card">
            <div class="card-body text-center">
                <i class="fas fa-user-plus fa-3x text-info mb-3"></i>
                <h5>Convertir a Colegiado</h5>
                <p>Convierte personas a colegiados</p>
                <a href="convertir-colegiado.php" class="btn btn-info">Convertir</a>
            </div>
        </div>
    </div>
</div>

<div class="row mt-4">
    <div class="col-md-6">
        <div class="card">
            <div class="card-header bg-primary text-white">
                <h5>Acciones Rápidas</h5>
            </div>
            <div class="card-body">
                <ul class="list-group">
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Ver Colegiados
                        <a href="colegiados.php" class="btn btn-sm btn-primary">Ver</a>
                    </li>
                    <li class="list-group-item d-flex justify-content-between align-items-center">
                        Reportes
                        <a href="#" class="btn btn-sm btn-secondary">Próximamente</a>
                    </li>
                </ul>
            </div>
        </div>
    </div>

    <div class="col-md-6">
        <div class="card">
            <div class="card-header bg-info text-white">
                <h5>Información del Sistema</h5>
            </div>
            <div class="card-body">
                <p><strong>Versión:</strong> <?= APP_VERSION ?></p>
                <p><strong>Backend API:</strong> <span class="badge bg-success">Conectado</span></p>
                <p><strong>Base de Datos:</strong> MySQL</p>
                <p><strong>Arquitectura:</strong> Spring Boot + PHP MVC</p>
            </div>
        </div>
    </div>
</div>

<?php require_once VIEWS_PATH . '/layout/footer.php'; ?>
