<?php
/**
 * Servicio para gestión de aportaciones
 * Consume los endpoints /aportaciones del backend
 */
class AportacionService
{
    private $api;

    public function __construct()
    {
        $this->api = new ApiClient();
    }

    /**
     * Obtiene aportaciones pendientes de una persona
     */
    public function obtenerPendientes($personaId)
    {
        return $this->api->get("/aportaciones/pendientes/{$personaId}");
    }

    /**
     * Obtiene todas las aportaciones de una persona
     */
    public function obtenerPorPersona($personaId)
    {
        return $this->api->get("/aportaciones/persona/{$personaId}");
    }

    /**
     * Obtiene el total de deuda de una persona
     */
    public function obtenerTotalDeuda($personaId)
    {
        return $this->api->get("/aportaciones/deuda/{$personaId}");
    }

    /**
     * Crea una aportación manual
     */
    public function crearManual($data)
    {
        return $this->api->post('/aportaciones/manual', $data);
    }

    /**
     * Crea aportaciones adelantadas
     */
    public function crearAdelantadas($data)
    {
        return $this->api->post('/aportaciones/adelantadas', $data);
    }

    /**
     * Anula una aportación
     */
    public function anular($id)
    {
        return $this->api->delete("/aportaciones/{$id}");
    }
}
