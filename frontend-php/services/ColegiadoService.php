<?php
/**
 * Servicio para gestión de colegiados
 * Consume los endpoints /colegiados del backend
 */
class ColegiadoService
{
    private $api;

    public function __construct()
    {
        $this->api = new ApiClient();
    }

    /**
     * Convierte una persona a colegiado
     */
    public function convertirPersonaAColegiado($data)
    {
        return $this->api->post('/colegiados/convertir', $data);
    }

    /**
     * Obtiene un colegiado por ID de persona
     */
    public function obtenerPorPersonaId($personaId)
    {
        return $this->api->get("/colegiados/persona/{$personaId}");
    }

    /**
     * Obtiene un colegiado por número de colegiatura
     */
    public function obtenerPorNumeroColegiatura($numero)
    {
        return $this->api->get("/colegiados/numero/{$numero}");
    }

    /**
     * Obtiene todos los colegiados
     */
    public function obtenerTodos()
    {
        return $this->api->get('/colegiados');
    }

    /**
     * Obtiene colegiados habilitados
     */
    public function obtenerHabilitados()
    {
        return $this->api->get('/colegiados/habilitados');
    }

    /**
     * Actualiza un colegiado
     */
    public function actualizar($id, $data)
    {
        return $this->api->put("/colegiados/{$id}", $data);
    }

    /**
     * Habilita un colegiado
     */
    public function habilitar($id, $observaciones = '')
    {
        return $this->api->patch("/colegiados/{$id}/habilitar", [
            'observaciones' => $observaciones
        ]);
    }

    /**
     * Inhabilita un colegiado
     */
    public function inhabilitar($id, $observaciones = '')
    {
        return $this->api->patch("/colegiados/{$id}/inhabilitar", [
            'observaciones' => $observaciones
        ]);
    }
}
