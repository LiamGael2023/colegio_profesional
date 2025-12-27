<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador del módulo de caja (pagos)
 */
class CajaController
{
    private $personaModel;
    private $aportacionModel;
    private $pagoModel;

    public function __construct()
    {
        // Verificar autenticación
        requireAuth();

        $this->personaModel = new Persona();
        $this->aportacionModel = new Aportacion();
        $this->pagoModel = new Pago();
    }

    /**
     * Muestra el módulo de caja
     */
    public function index()
    {
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

        $aportaciones = $this->aportacionModel->obtenerPendientes($personaId);

        echo json_encode(['success' => true, 'data' => $aportaciones]);
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

        $result = $this->pagoModel->procesar($data);

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

        $historial = $this->pagoModel->obtenerHistorial($personaId);

        echo json_encode(['success' => true, 'data' => $historial]);
        exit;
    }

    /**
     * Obtiene el resumen del día
     */
    public function resumenDelDia()
    {
        header('Content-Type: application/json');

        $pagos = $this->pagoModel->obtenerPagosDelDia();
        $total = array_sum(array_column($pagos, 'monto_total'));

        echo json_encode([
            'success' => true,
            'data' => [
                'pagos' => $pagos,
                'totalPagos' => count($pagos),
                'totalRecaudado' => $total
            ]
        ]);
        exit;
    }
}
