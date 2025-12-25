<?php
/**
 * Servicio para gestión de pagos
 * Consume los endpoints /pagos del backend
 */
class PagoService
{
    private $api;

    public function __construct()
    {
        $this->api = new ApiClient();
    }

    /**
     * Procesa un pago
     */
    public function procesarPago($data)
    {
        return $this->api->post('/pagos', $data);
    }

    /**
     * Obtiene el historial de pagos de una persona
     */
    public function obtenerHistorial($personaId)
    {
        return $this->api->get("/pagos/persona/{$personaId}");
    }

    /**
     * Obtiene un pago por ID
     */
    public function obtenerPorId($id)
    {
        return $this->api->get("/pagos/{$id}");
    }

    /**
     * Obtiene los detalles de un pago
     */
    public function obtenerDetalles($id)
    {
        return $this->api->get("/pagos/{$id}/detalles");
    }

    /**
     * Obtiene el total pagado por una persona
     */
    public function obtenerTotalPagado($personaId)
    {
        return $this->api->get("/pagos/total/{$personaId}");
    }

    /**
     * Obtiene los pagos del día
     */
    public function obtenerPagosDelDia()
    {
        return $this->api->get('/pagos/hoy');
    }

    /**
     * Obtiene el total recaudado en un período
     */
    public function obtenerTotalRecaudado($fechaInicio, $fechaFin)
    {
        return $this->api->get('/pagos/recaudado', [
            'fechaInicio' => $fechaInicio,
            'fechaFin' => $fechaFin
        ]);
    }
}
