<?php
/**
 * Cliente HTTP para consumir la API REST
 * Utiliza cURL para hacer peticiones al backend Spring Boot
 */
class ApiClient
{
    private $baseUrl;
    private $token;

    public function __construct()
    {
        $this->baseUrl = API_BASE_URL;
        $this->token = $_SESSION['auth_token'] ?? null;
    }

    /**
     * Realiza una petición GET
     */
    public function get($endpoint, $params = [])
    {
        $url = $this->baseUrl . $endpoint;

        if (!empty($params)) {
            $url .= '?' . http_build_query($params);
        }

        return $this->request('GET', $url);
    }

    /**
     * Realiza una petición POST
     */
    public function post($endpoint, $data = [])
    {
        $url = $this->baseUrl . $endpoint;
        return $this->request('POST', $url, $data);
    }

    /**
     * Realiza una petición PUT
     */
    public function put($endpoint, $data = [])
    {
        $url = $this->baseUrl . $endpoint;
        return $this->request('PUT', $url, $data);
    }

    /**
     * Realiza una petición DELETE
     */
    public function delete($endpoint)
    {
        $url = $this->baseUrl . $endpoint;
        return $this->request('DELETE', $url);
    }

    /**
     * Realiza una petición PATCH
     */
    public function patch($endpoint, $data = [])
    {
        $url = $this->baseUrl . $endpoint;
        return $this->request('PATCH', $url, $data);
    }

    /**
     * Método privado para realizar peticiones HTTP
     */
    private function request($method, $url, $data = null)
    {
        $ch = curl_init();

        curl_setopt($ch, CURLOPT_URL, $url);
        curl_setopt($ch, CURLOPT_RETURNTRANSFER, true);
        curl_setopt($ch, CURLOPT_CUSTOMREQUEST, $method);

        $headers = [
            'Content-Type: application/json',
            'Accept: application/json'
        ];

        // Agregar token de autenticación si existe
        if ($this->token) {
            $headers[] = 'Authorization: Bearer ' . $this->token;
        }

        curl_setopt($ch, CURLOPT_HTTPHEADER, $headers);

        // Agregar datos si es POST, PUT o PATCH
        if ($data !== null && in_array($method, ['POST', 'PUT', 'PATCH'])) {
            curl_setopt($ch, CURLOPT_POSTFIELDS, json_encode($data));
        }

        $response = curl_exec($ch);
        $httpCode = curl_getinfo($ch, CURLINFO_HTTP_CODE);
        $error = curl_error($ch);

        curl_close($ch);

        if ($error) {
            return [
                'success' => false,
                'error' => 'Error de conexión: ' . $error,
                'httpCode' => 0
            ];
        }

        $result = [
            'success' => $httpCode >= 200 && $httpCode < 300,
            'httpCode' => $httpCode,
            'data' => json_decode($response, true)
        ];

        // Manejar errores de autenticación
        if ($httpCode === 401) {
            $this->handleUnauthorized();
        }

        return $result;
    }

    /**
     * Maneja respuestas no autorizadas
     */
    private function handleUnauthorized()
    {
        // Limpiar sesión y redirigir a login
        unset($_SESSION['auth_token']);
        unset($_SESSION['user']);
    }

    /**
     * Establece el token de autenticación
     */
    public function setToken($token)
    {
        $this->token = $token;
    }
}
