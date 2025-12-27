<?php
require_once __DIR__ . '/../config/config.php';

/**
 * Controlador para búsqueda de personas
 */
class BuscadorController
{
    private $personaModel;
    private $colegiadoModel;

    public function __construct()
    {
        requireAuth();
        $this->personaModel = new Persona();
        $this->colegiadoModel = new Colegiado();
    }

    /**
     * Muestra el buscador
     */
    public function index()
    {
        $user = [
            'nombreCompleto' => $_SESSION['user_nombre'],
            'rol' => $_SESSION['user_rol']
        ];
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

        $persona = $this->personaModel->buscarPorDni($dni);

        if ($persona) {
            // Verificar si es colegiado
            $colegiado = $this->colegiadoModel->obtenerPorPersonaId($persona['id']);
            $persona['esColegiado'] = ($colegiado !== false);
            if ($colegiado) {
                $persona['colegiado'] = $colegiado;
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

        $personas = $this->personaModel->buscar($criterio);

        echo json_encode(['success' => true, 'data' => $personas]);
        exit;
    }
}
