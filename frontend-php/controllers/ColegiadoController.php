<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador para gestión de colegiados
 */
class ColegiadoController
{
    private $personaModel;
    private $colegiadoModel;

    public function __construct()
    {
        // Verificar autenticación
        requireAuth();

        $this->personaModel = new Persona();
        $this->colegiadoModel = new Colegiado();
    }

    /**
     * Muestra el formulario de conversión a colegiado
     */
    public function index()
    {
        require_once VIEWS_PATH . '/colegiado/convertir.php';
    }

    /**
     * Convierte una persona a colegiado
     */
    public function convertir()
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

        $success = $this->colegiadoModel->convertir($data);

        if ($success) {
            echo json_encode(['success' => true, 'message' => 'Persona convertida a colegiado exitosamente']);
        } else {
            echo json_encode(['success' => false, 'error' => 'Error al convertir a colegiado']);
        }
        exit;
    }

    /**
     * Lista todos los colegiados
     */
    public function listar()
    {
        $colegiados = $this->colegiadoModel->obtenerTodos();

        require_once VIEWS_PATH . '/colegiado/listar.php';
    }
}
