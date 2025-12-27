# 🎓 Sistema de Gestión de Colegio Profesional

Sistema completo de gestión para Colegios Profesionales desarrollado en **PHP 8.1 MVC puro** con MySQL 8.0.

## 🚀 Opciones de Instalación

Puedes ejecutar este sistema de dos maneras:

### 🐳 Opción 1: Docker (Recomendado)
La forma más rápida y sencilla. Todo funciona con un solo comando.

**[📖 Ver Instrucciones Docker](README_DOCKER.md)**

```bash
docker-compose up -d
# Acceder a: http://localhost:8080
```

### 💻 Opción 2: XAMPP (Manual)
Para desarrollo local o servidores con XAMPP/WAMP.

**[📖 Ver Instrucciones XAMPP](README_PHP_MVC.md)**

## ⚡ Inicio Rápido con Docker

```bash
# 1. Clonar repositorio
git clone https://github.com/LiamGael2023/colegio_profesional.git
cd colegio_profesional

# 2. Levantar contenedores
docker-compose up -d

# 3. Acceder al sistema
# URL: http://localhost:8080
# Usuario: admin
# Contraseña: admin123
```

## ✨ Características Principales

### 👥 Gestión de Personas
- Alta de personas (DNI, nombres, dirección, teléfono, email)
- Búsqueda por DNI o nombre
- Edición y actualización de datos
- Validación de DNI único

### 🎓 Gestión de Colegiados
- Conversión de persona a colegiado
- Asignación de número de colegiatura
- Registro de fecha de colegiatura
- Especialidades y subespecialidades
- Estados: Habilitado/Inhabilitado automático

### 💰 Sistema de Cuotas
- Generación automática mensual (día 1 de cada mes)
- Cuota mensual configurable (default: S/. 150.00)
- **Anti-duplicados**: Restricción única por persona/mes/año
- Estados: Pendiente/Pagado/Vencido
- Tolerancia de impago configurable (default: 3 meses)

### 🏦 Caja y Pagos
- Procesamiento de pagos múltiples obligaciones
- Generación automática de recibos
- Historial completo de pagos por colegiado
- Resumen diario de recaudación
- Métodos de pago: Efectivo, Transferencia, Tarjeta

### 📊 Reportes y Consultas
- Estado de cuenta por colegiado
- Deudas pendientes
- Recaudación diaria/mensual/anual
- Listado de habilitados/inhabilitados
- Auditoría de operaciones

### 🔐 Seguridad
- Autenticación con PHP sessions
- Contraseñas hasheadas con `password_hash()`
- Prepared statements (PDO)
- Control de acceso por roles (Admin/Cajero)
- Auditoría de cambios

## 🗄️ Estructura de la Base de Datos

```sql
personas          -- Datos personales básicos
colegiados        -- Información de colegiación
aportaciones      -- Cuotas mensuales (con UNIQUE KEY anti-duplicados)
pagos             -- Registro de pagos
detalle_pagos     -- Detalle de cada pago
usuarios          -- Usuarios del sistema
configuracion     -- Parámetros del sistema
auditoria         -- Log de operaciones
```

## 🏗️ Arquitectura

```
frontend-php/
├── config/           # Configuración y autoloader
├── controllers/      # Lógica de negocio
│   ├── AuthController.php
│   ├── BuscadorController.php
│   ├── CajaController.php
│   └── ColegiadoController.php
├── models/           # Acceso a datos (PDO)
│   ├── Usuario.php
│   ├── Persona.php
│   ├── Colegiado.php
│   ├── Aportacion.php
│   └── Pago.php
├── views/            # Interfaz de usuario
├── helpers/          # Utilidades (Database.php)
├── public/           # Assets (CSS, JS, imágenes)
└── .htaccess         # Configuración Apache
```

## 🔧 Tecnologías

- **Backend**: PHP 8.1 (MVC puro, sin frameworks)
- **Base de Datos**: MySQL 8.0
- **Servidor Web**: Apache 2.4
- **Frontend**: HTML5, CSS3, JavaScript (Vanilla)
- **Containerización**: Docker & Docker Compose
- **Seguridad**: PDO Prepared Statements, password_hash()

## 📦 Servicios Docker

| Servicio | Puerto | Descripción |
|----------|--------|-------------|
| Web (Apache+PHP) | 8080 | Aplicación principal |
| MySQL | 3306 | Base de datos |
| phpMyAdmin | 8081 | Gestor de BD |

## 🔄 Tareas Automáticas

### Generación de Cuotas Mensuales
El sistema incluye un modelo para generar cuotas automáticamente:

```php
$aportacionModel = new Aportacion();
$generadas = $aportacionModel->generarCuotasMensuales($mes, $anio);
```

**Configurar con Cron Job:**
```bash
0 0 1 * * /usr/bin/php /ruta/cron/generar_cuotas.php
```

Ver `README_PHP_MVC.md` para más detalles.

## 🎯 Flujo de Trabajo Típico

1. **Alta de Persona** → Búsqueda por DNI → Registro de datos
2. **Conversión a Colegiado** → Número de colegiatura → Fecha de alta
3. **Generación de Cuotas** → Automática (día 1 de cada mes)
4. **Pago en Caja** → Búsqueda por DNI → Selección de deudas → Pago
5. **Habilitación Automática** → Si está al día (≤3 meses impagos)

## 👥 Usuarios por Defecto

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| admin | admin123 | Administrador |
| cajero | admin123 | Cajero |

**⚠️ IMPORTANTE**: Cambiar contraseñas en producción.

## 🐛 Solución de Problemas

### Docker
```bash
# Ver logs
docker-compose logs -f

# Reiniciar servicios
docker-compose restart

# Reconstruir
docker-compose up -d --build
```

### XAMPP
- Verificar que Apache y MySQL estén corriendo
- Revisar `config.php` para credenciales de BD
- Importar `database/schema.sql`

## 📚 Documentación Adicional

- [📖 Guía completa Docker](README_DOCKER.md)
- [📖 Guía completa XAMPP/PHP](README_PHP_MVC.md)

## 🤝 Contribuciones

Las contribuciones son bienvenidas. Por favor:
1. Fork el proyecto
2. Crea una rama para tu feature (`git checkout -b feature/AmazingFeature`)
3. Commit tus cambios (`git commit -m 'Add some AmazingFeature'`)
4. Push a la rama (`git push origin feature/AmazingFeature`)
5. Abre un Pull Request

## 📝 Changelog

### v1.0.0 (2025)
- ✅ Migración completa a PHP MVC puro
- ✅ Eliminación de dependencias Spring Boot
- ✅ Implementación de modelos PDO
- ✅ Sistema de autenticación con sesiones
- ✅ Dockerización completa
- ✅ Documentación exhaustiva

## 📄 Licencia

Este proyecto es de uso educativo y profesional.

## 📞 Soporte

Para reportar bugs o solicitar features:
- [Crear Issue](https://github.com/LiamGael2023/colegio_profesional/issues)

---

**Desarrollado con ❤️ usando PHP, MySQL y Docker**
