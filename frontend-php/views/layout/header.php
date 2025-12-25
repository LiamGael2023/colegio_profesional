<!DOCTYPE html>
<html lang="es">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><?= $pageTitle ?? APP_NAME ?></title>

    <!-- Bootstrap CSS -->
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.0/dist/css/bootstrap.min.css" rel="stylesheet">

    <!-- Font Awesome -->
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">

    <!-- Custom CSS -->
    <style>
        .sidebar {
            min-height: 100vh;
            background: #2c3e50;
        }
        .sidebar a {
            color: #ecf0f1;
            text-decoration: none;
            padding: 15px;
            display: block;
            transition: all 0.3s;
        }
        .sidebar a:hover {
            background: #34495e;
        }
        .sidebar a.active {
            background: #3498db;
        }
    </style>
</head>
<body>
    <div class="container-fluid">
        <div class="row">
            <!-- Sidebar -->
            <nav class="col-md-2 d-md-block sidebar">
                <div class="position-sticky pt-3">
                    <h4 class="text-white text-center mb-4">
                        <i class="fas fa-graduation-cap"></i>
                        Colegio Profesional
                    </h4>

                    <ul class="nav flex-column">
                        <li class="nav-item">
                            <a class="nav-link" href="/dashboard.php">
                                <i class="fas fa-home"></i> Dashboard
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/buscador.php">
                                <i class="fas fa-search"></i> Buscar Persona
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/caja.php">
                                <i class="fas fa-cash-register"></i> Módulo de Caja
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/convertir-colegiado.php">
                                <i class="fas fa-user-plus"></i> Convertir a Colegiado
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/colegiados.php">
                                <i class="fas fa-users"></i> Colegiados
                            </a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/reportes.php">
                                <i class="fas fa-chart-bar"></i> Reportes
                            </a>
                        </li>
                        <li class="nav-item mt-4">
                            <a class="nav-link text-danger" href="/logout.php">
                                <i class="fas fa-sign-out-alt"></i> Cerrar Sesión
                            </a>
                        </li>
                    </ul>

                    <?php if (isset($user)): ?>
                    <div class="mt-4 p-3 text-white">
                        <small>
                            <strong><?= htmlspecialchars($user['nombreCompleto']) ?></strong><br>
                            <span class="badge bg-info"><?= htmlspecialchars($user['rol']) ?></span>
                        </small>
                    </div>
                    <?php endif; ?>
                </div>
            </nav>

            <!-- Main Content -->
            <main class="col-md-10 ms-sm-auto px-md-4">
                <div class="pt-3 pb-2 mb-3 border-bottom">
                    <h1 class="h2"><?= $pageTitle ?? 'Dashboard' ?></h1>
                </div>
