<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador para búsqueda de personas
 */
class BuscadorController
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
     * Muestra el buscador
     */
    public function index()
    {
        $user = $this->authService->getCurrentUser();
        require_once VIEWS_PATH . '/buscador/index.php';
    }

    /**
     * Busca una persona por DNI (AJAX)
     */
    public function buscarPorDni()
    {
        header('Content-Type: application/json');

        $dni = $_GET['dni'] ?? '';

        if (empty($dni)) {
            echo json_encode(['success' => false, 'error' => 'DNI requerido']);
            exit;
        }

        $result = $this->personaService->buscarPorDni($dni);

        if ($result['success']) {
            $persona = $result['data'];

            // Verificar si es colegiado
            $colegiadoResult = $this->colegiadoService->obtenerPorPersonaId($persona['id']);
            $persona['esColegiado'] = $colegiadoResult['success'];
            if ($colegiadoResult['success']) {
                $persona['colegiado'] = $colegiadoResult['data'];
            }

            echo json_encode(['success' => true, 'data' => $persona]);
        } else {
            echo json_encode(['success' => false, 'error' => 'Persona no encontrada']);
        }
        exit;
    }

    /**
     * Busca personas por criterio (AJAX)
     */
    public function buscar()
    {
        header('Content-Type: application/json');

        $criterio = $_GET['criterio'] ?? '';

        if (empty($criterio)) {
            echo json_encode(['success' => false, 'error' => 'Criterio de búsqueda requerido']);
            exit;
        }

        $result = $this->personaService->buscar($criterio);

        echo json_encode($result);
        exit;
    }
}
