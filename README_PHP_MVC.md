# Sistema PHP MVC - Colegio Profesional

## ✅ Lo que se ha completado:

### 1. Configuración
- ✅ `config/config.php` - Configuración de BD MySQL y sistema

### 2. Base de Datos
- ✅ Clase `Database` - Conexión PDO a MySQL

### 3. Modelos (Acceso directo a MySQL)
- ✅ `Usuario.php` - Login, gestión de usuarios
- ✅ `Persona.php` - CRUD de personas
- ✅ `Colegiado.php` - Gestión de colegiados
- ✅ `Aportacion.php` - Gestión de aportaciones/cuotas
- ✅ `Pago.php` - Procesamiento de pagos

### 4. Controladores Actualizados
- ✅ `AuthController.php` - Login con sesiones PHP

## 🚀 Cómo usar el sistema PHP MVC:

### 1. Configuración de BD

Editar `frontend-php/config/config.php`:

```php
define('DB_HOST', 'localhost');
define('DB_NAME', 'colegio_profesional');
define('DB_USER', 'root');
define('DB_PASS', ''); // Vacío en XAMPP por defecto
```

### 2. Importar Base de Datos

```bash
# En phpMyAdmin o MySQL:
mysql -u root colegio_profesional < database/schema.sql
```

### 3. Copiar a XAMPP

```bash
xcopy /E /I frontend-php C:\xampp\htdocs\colegio
```

### 4. Acceder al Sistema

```
http://localhost/colegio/
```

**Credenciales:**
- Usuario: `admin` / Contraseña: `admin123`
- Usuario: `cajero` / Contraseña: `admin123`

## 📁 Estructura de Archivos:

```
frontend-php/
├── config/
│   └── config.php          # Configuración BD y sistema
│
├── helpers/
│   └── Database.php        # Clase de conexión PDO
│
├── models/                 # Modelos (acceso a MySQL)
│   ├── Usuario.php
│   ├── Persona.php
│   ├── Colegiado.php
│   ├── Aportacion.php
│   └── Pago.php
│
├── controllers/            # Controladores
│   ├── AuthController.php ✅ (Actualizado)
│   ├── BuscadorController.php (Pendiente actualizar)
│   ├── CajaController.php  (Pendiente actualizar)
│   └── ColegiadoController.php (Pendiente actualizar)
│
└── views/                  # Vistas HTML/PHP
    ├── auth/
    ├── layout/
    ├── buscador/
    ├── caja/
    └── colegiado/
```

## 🔧 Controladores Pendientes de Actualizar:

### BuscadorController.php

Cambiar de:
```php
$this->personaService = new PersonaService(); // API REST
```

A:
```php
$this->personaModel = new Persona(); // MySQL directo
```

### CajaController.php

Cambiar de:
```php
$this->pagoService->procesarPago($data); // API REST
```

A:
```php
$this->pagoModel->procesar($data); // MySQL directo
```

### ColegiadoController.php

Cambiar de:
```php
$this->colegiadoService->convertirPersonaAColegiado($data); // API REST
```

A:
```php
$this->colegiadoModel->convertir($data); // MySQL directo
```

## 💡 Ejemplo de Uso de los Modelos:

### Buscar Persona:
```php
$personaModel = new Persona();
$persona = $personaModel->buscarPorDni('12345678');
```

### Procesar Pago:
```php
$pagoModel = new Pago();
$result = $pagoModel->procesar([
    'persona_id' => 1,
    'aportaciones_ids' => [1, 2, 3],
    'metodo_pago' => 'EFECTIVO'
]);
```

### Convertir a Colegiado:
```php
$colegiadoModel = new Colegiado();
$colegiadoModel->convertir([
    'persona_id' => 1,
    'numero_colegiatura' => 'COL-2025-001',
    'especialidad' => 'Ingeniería',
    'universidad' => 'UNMSM'
]);
```

## 🔄 Funciones Automáticas (Cron Jobs):

### Generar Cuotas Mensuales:

Crear archivo `cron/generar-cuotas.php`:

```php
<?php
require_once __DIR__ . '/../config/config.php';

$aportacionModel = new Aportacion();
$mes = date('n');
$anio = date('Y');

$generadas = $aportacionModel->generarCuotasMensuales($mes, $anio);
echo "Cuotas generadas: $generadas\n";
```

**Programar en cron (día 1 de cada mes):**
```bash
0 0 1 * * php /path/to/colegio/cron/generar-cuotas.php
```

### Verificar Habilitaciones:

Crear archivo `cron/verificar-habilitaciones.php`:

```php
<?php
require_once __DIR__ . '/../config/config.php';

$colegiadoModel = new Colegiado();
$colegiados = $colegiadoModel->obtenerHabilitados();

foreach ($colegiados as $colegiado) {
    if ($colegiado['meses_impagos_consecutivos'] >= MESES_TOLERANCIA_IMPAGO) {
        $colegiadoModel->inhabilitar(
            $colegiado['colegiado_id'],
            "Inhabilitado por {$colegiado['meses_impagos_consecutivos']} meses impagos"
        );
    }
}
```

## 📊 Características Implementadas:

### Modelos:
- ✅ Login con sesiones PHP (sin JWT)
- ✅ CRUD completo de Personas
- ✅ Conversión a Colegiado
- ✅ Generación de cuotas (manual y automática)
- ✅ Regla anti-duplicados (persona_id + mes + anio)
- ✅ Procesamiento de pagos
- ✅ Actualización automática de habilitación
- ✅ Estado de cuenta
- ✅ Reportes y estadísticas

### Seguridad:
- ✅ Contraseñas hasheadas con `password_hash()`
- ✅ Prepared statements (PDO)
- ✅ Sesiones PHP seguras
- ✅ Validación de entrada

## 🎯 Ventajas de esta Arquitectura:

1. **Sin dependencias externas** - Solo PHP + MySQL
2. **Compatible con XAMPP** - Plug & play
3. **Sin compilación** - No necesitas Maven/Java
4. **Fácil de mantener** - Código PHP simple
5. **Rápido** - Acceso directo a MySQL
6. **Escalable** - Puedes agregar APIs después

## 📝 Notas:

- Los archivos en `services/` (ApiClient, AuthService, etc.) ya NO se usan
- El backend Spring Boot ya NO es necesario
- Todo funciona con PHP + MySQL directamente
- Puedes crear tus propias APIs REST después si lo deseas

## 🚨 Importante:

El sistema ahora es **100% PHP MVC**. No necesitas:
- ❌ Java
- ❌ Maven
- ❌ Spring Boot
- ❌ JWT

Solo necesitas:
- ✅ PHP 8.0+
- ✅ MySQL 8.0+
- ✅ XAMPP (Apache + MySQL)

