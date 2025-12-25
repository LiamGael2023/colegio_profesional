<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador para gestión de colegiados
 */
class ColegiadoController
{
    private $personaService;
    private $colegiadoService;
    private $authService;

    public function __construct()
    {
        $this->authService = new AuthService();
        $this->personaService = new PersonaService();
        $this->colegiadoService = new ColegiadoService();

        // Verificar autenticación
        if (!$this->authService->isAuthenticated()) {
            header('Location: /login.php');
            exit;
        }
    }

    /**
     * Muestra el formulario de conversión a colegiado
     */
    public function index()
    {
        $user = $this->authService->getCurrentUser();
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

        $result = $this->colegiadoService->convertirPersonaAColegiado($data);

        echo json_encode($result);
        exit;
    }

    /**
     * Lista todos los colegiados
     */
    public function listar()
    {
        $user = $this->authService->getCurrentUser();
        $result = $this->colegiadoService->obtenerTodos();

        $colegiados = $result['success'] ? $result['data'] : [];

        require_once VIEWS_PATH . '/colegiado/listar.php';
    }
}
