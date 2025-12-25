<?php
/**
 * Servicio para gestión de personas
 * Consume los endpoints /personas del backend
 */
class PersonaService
{
    private $api;

    public function __construct()
    {
        $this->api = new ApiClient();
    }

    /**
     * Busca una persona por DNI
     */
    public function buscarPorDni($dni)
    {
        return $this->api->get("/personas/dni/{$dni}");
    }

    /**
     * Busca personas por criterio (DNI o nombre)
     */
    public function buscar($criterio)
    {
        return $this->api->get('/personas/buscar', ['criterio' => $criterio]);
    }

    /**
     * Obtiene una persona por ID
     */
    public function obtenerPorId($id)
    {
        return $this->api->get("/personas/{$id}");
    }

    /**
     * Obtiene todas las personas activas
     */
    public function obtenerActivas()
    {
        return $this->api->get('/personas/activas');
    }

    /**
     * Crea una nueva persona
     */
    public function crear($data)
    {
        return $this->api->post('/personas', $data);
    }

    /**
     * Actualiza una persona
     */
    public function actualizar($id, $data)
    {
        return $this->api->put("/personas/{$id}", $data);
    }

    /**
     * Desactiva una persona
     */
    public function desactivar($id)
    {
        return $this->api->delete("/personas/{$id}");
    }
}
