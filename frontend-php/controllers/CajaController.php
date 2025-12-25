<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador del módulo de caja (pagos)
 */
class CajaController
{
    private $personaService;
    private $aportacionService;
    private $pagoService;
    private $authService;

    public function __construct()
    {
        $this->authService = new AuthService();
        $this->personaService = new PersonaService();
        $this->aportacionService = new AportacionService();
        $this->pagoService = new PagoService();

        // Verificar autenticación
        if (!$this->authService->isAuthenticated()) {
            header('Location: /login.php');
            exit;
        }
    }

    /**
     * Muestra el módulo de caja
     */
    public function index()
    {
        $user = $this->authService->getCurrentUser();
        require_once VIEWS_PATH . '/caja/index.php';
    }

    /**
     * Obtiene las aportaciones pendientes de una persona (AJAX)
     */
    public function obtenerAportacionesPendientes()
    {
        header('Content-Type: application/json');

        $personaId = $_GET['personaId'] ?? null;

        if (!$personaId) {
            echo json_encode(['success' => false, 'error' => 'ID de persona requerido']);
            exit;
        }

        $result = $this->aportacionService->obtenerPendientes($personaId);

        echo json_encode($result);
        exit;
    }

    /**
     * Procesa un pago
     */
    public function procesarPago()
    {
        header('Content-Type: application/json');

        if ($_SERVER['REQUEST_METHOD'] !== 'POST') {
            echo json_encode(['success' => false, 'error' => 'Método no permitido']);
            exit;
        }

        $data = json_decode(file_get_contents('php://input'), true);

        if (!$data) {
            echo json_encode(['success' => false, 'error' => 'Datos inválidos']);
            exit;
        }

        $result = $this->pagoService->procesarPago($data);

        echo json_encode($result);
        exit;
    }

    /**
     * Obtiene el historial de pagos de una persona (AJAX)
     */
    public function obtenerHistorial()
    {
        header('Content-Type: application/json');

        $personaId = $_GET['personaId'] ?? null;

        if (!$personaId) {
            echo json_encode(['success' => false, 'error' => 'ID de persona requerido']);
            exit;
        }

        $result = $this->pagoService->obtenerHistorial($personaId);

        echo json_encode($result);
        exit;
    }

    /**
     * Obtiene el resumen del día
     */
    public function resumenDelDia()
    {
        header('Content-Type: application/json');

        $result = $this->pagoService->obtenerPagosDelDia();

        if ($result['success']) {
            $pagos = $result['data'];
            $total = array_sum(array_column($pagos, 'montoTotal'));

            echo json_encode([
                'success' => true,
                'data' => [
                    'pagos' => $pagos,
                    'totalPagos' => count($pagos),
                    'totalRecaudado' => $total
                ]
            ]);
        } else {
            echo json_encode($result);
        }
        exit;
    }
}
