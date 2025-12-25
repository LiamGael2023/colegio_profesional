# Sistema de Gestión de Colegio Profesional

Sistema integral de gestión para un colegio profesional con arquitectura desacoplada: **Backend en Spring Boot (API REST)** y **Frontend Administrativo en PHP MVC** (Consumidor de API), utilizando **MySQL** como base de datos única.

## Arquitectura del Sistema

```
┌──────────────────────────────────────┐
│     Frontend Web (PHP MVC)           │
│  - Vistas (Bootstrap + jQuery)       │
│  - Controladores PHP                 │
│  - Servicios API Client (cURL)       │
└──────────────┬───────────────────────┘
               │
               │ HTTP/JSON (API REST)
               │ JWT Authentication
               │
┌──────────────▼───────────────────────┐
│     Backend (Spring Boot)            │
│  - Controllers REST                  │
│  - Services (Business Logic)         │
│  - Repositories (JPA)                │
│  - Security (JWT)                    │
│  - Scheduled Tasks (@Scheduled)      │
└──────────────┬───────────────────────┘
               │
               │ JPA/Hibernate
               │
┌──────────────▼───────────────────────┐
│        MySQL Database                │
│  - personas                          │
│  - colegiados                        │
│  - aportaciones                      │
│  - pagos                             │
│  - usuarios                          │
└──────────────────────────────────────┘
```

## Características Principales

### 1. Gestión de Personas y Colegiados
- ✅ Registro de personas (Público General)
- ✅ Conversión de persona a colegiado
- ✅ Asignación de número de colegiatura
- ✅ Gestión de especialidades y universidades

### 2. Sistema de Aportaciones
- ✅ **Generación automática de cuotas mensuales** (@Scheduled)
- ✅ Se ejecuta el día 1 de cada mes a las 00:01
- ✅ Solo para colegiados HABILITADOS
- ✅ **Regla anti-duplicados** (UNIQUE KEY: persona_id, mes, anio)
- ✅ Pagos adelantados (creación manual de obligaciones futuras)
- ✅ Verificación automática: `existsByPersonaIdAndMesAndAnio`

### 3. Habilitación Automática
- ✅ **Inhabilitación automática** por meses impagos
- ✅ Proceso diario de verificación (@Scheduled a las 02:00 AM)
- ✅ Configuración de meses de tolerancia
- ✅ Actualización automática de contadores

### 4. Módulo de Caja
- ✅ Búsqueda de personas por DNI
- ✅ Visualización de deudas pendientes
- ✅ Selección múltiple de obligaciones
- ✅ Procesamiento de pagos
- ✅ Múltiples métodos de pago (Efectivo, Tarjeta, Yape, Plin, etc.)
- ✅ Generación de comprobantes

### 5. Seguridad
- ✅ **Autenticación JWT** (JSON Web Tokens)
- ✅ Compatible con Web y App Móvil
- ✅ Roles de usuario (ADMIN, CAJERO, SECRETARIA, AUDITOR)
- ✅ Tokens con expiración de 24 horas

### 6. Reportes
- ✅ Estados de cuenta históricos
- ✅ Reportes unificados (público + colegiado)
- ✅ Recaudación por período
- ✅ Estadísticas de pagos

## Tecnologías Utilizadas

### Backend
- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security + JWT**
- **Spring Data JPA**
- **MySQL Connector**
- **Lombok**
- **Maven**
- **Springdoc OpenAPI (Swagger)**

### Frontend
- **PHP 8.x**
- **Bootstrap 5**
- **jQuery**
- **Font Awesome**

### Base de Datos
- **MySQL 8.x**

## Estructura del Proyecto

```
colegio_profesional/
├── backend/                          # Backend Spring Boot
│   ├── src/main/java/com/colegio/
│   │   ├── controller/               # Controladores REST
│   │   ├── service/                  # Servicios de negocio
│   │   ├── repository/               # Repositorios JPA
│   │   ├── model/                    # Entidades y DTOs
│   │   ├── security/                 # Configuración de seguridad
│   │   └── ColegioApplication.java
│   ├── src/main/resources/
│   │   └── application.yml           # Configuración
│   └── pom.xml
│
├── frontend-php/                     # Frontend PHP MVC
│   ├── config/                       # Configuración
│   ├── controllers/                  # Controladores PHP
│   ├── services/                     # Servicios API Client
│   ├── views/                        # Vistas HTML/PHP
│   └── public/                       # Archivos públicos
│
├── database/                         # Scripts de base de datos
│   └── schema.sql                    # Schema completo
│
└── README.md                         # Este archivo
```

## Instalación y Configuración

### Requisitos Previos
- Java 17 o superior
- MySQL 8.0 o superior
- PHP 8.0 o superior
- Maven 3.6+
- Servidor web (Apache/Nginx) con PHP

### 1. Base de Datos

```bash
# Crear la base de datos
mysql -u root -p < database/schema.sql
```

### 2. Backend (Spring Boot)

```bash
# Navegar al directorio backend
cd backend

# Configurar application.yml
# Editar src/main/resources/application.yml
# Actualizar las credenciales de MySQL

# Compilar
mvn clean install

# Ejecutar
mvn spring-boot:run

# El backend estará disponible en: http://localhost:8080/api
```

### 3. Frontend (PHP)

```bash
# Configurar PHP
cd frontend-php

# Editar config/config.php
# Actualizar la URL del backend si es necesario

# Configurar servidor web (Apache ejemplo)
# DocumentRoot: /path/to/colegio_profesional/frontend-php/public

# Acceder a: http://localhost/login.php
```

### Usuarios por Defecto

El sistema incluye dos usuarios precargados:

| Usuario | Contraseña | Rol   |
|---------|-----------|-------|
| admin   | admin123  | ADMIN |
| cajero  | admin123  | CAJERO|

## API Endpoints

### Autenticación
```
POST   /auth/login              # Login
GET    /auth/validate           # Validar token
GET    /auth/health             # Health check
```

### Personas
```
GET    /personas                # Listar todas
GET    /personas/{id}           # Obtener por ID
GET    /personas/dni/{dni}      # Buscar por DNI
GET    /personas/buscar?criterio=xxx  # Búsqueda general
POST   /personas                # Crear
PUT    /personas/{id}           # Actualizar
DELETE /personas/{id}           # Desactivar
```

### Colegiados
```
GET    /colegiados              # Listar todos
POST   /colegiados/convertir    # Convertir persona a colegiado
GET    /colegiados/{id}         # Obtener por ID
GET    /colegiados/numero/{num} # Obtener por número
GET    /colegiados/habilitados  # Listar habilitados
PATCH  /colegiados/{id}/habilitar    # Habilitar
PATCH  /colegiados/{id}/inhabilitar  # Inhabilitar
```

### Aportaciones
```
GET    /aportaciones/pendientes/{personaId}  # Pendientes
GET    /aportaciones/deuda/{personaId}       # Total deuda
POST   /aportaciones/manual                  # Crear manual
POST   /aportaciones/adelantadas             # Pagos adelantados
POST   /aportaciones/generar                 # Generar cuotas (ADMIN)
DELETE /aportaciones/{id}                    # Anular (ADMIN)
```

### Pagos
```
POST   /pagos                      # Procesar pago
GET    /pagos/{id}                 # Obtener por ID
GET    /pagos/persona/{personaId}  # Historial
GET    /pagos/hoy                  # Pagos del día
GET    /pagos/recaudado?fechaInicio=xxx&fechaFin=xxx
```

## Documentación Swagger

Una vez iniciado el backend, la documentación interactiva está disponible en:
```
http://localhost:8080/api/swagger-ui.html
```

## Procesos Automáticos

### Generación de Cuotas Mensuales
- **Frecuencia**: Día 1 de cada mes a las 00:01
- **Acción**: Genera obligaciones para todos los colegiados HABILITADOS
- **Protección**: Verificación anti-duplicados automática

### Verificación de Habilitaciones
- **Frecuencia**: Todos los días a las 02:00 AM
- **Acciones**:
  - Actualiza contadores de meses impagos
  - Inhabilita automáticamente colegiados que exceden tolerancia

## Configuración del Sistema

Las configuraciones se almacenan en la tabla `configuracion`:

| Clave | Valor Default | Descripción |
|-------|--------------|-------------|
| CUOTA_MENSUAL_DEFAULT | 150.00 | Monto de cuota mensual |
| MESES_TOLERANCIA_IMPAGO | 3 | Meses antes de inhabilitar |
| DIA_GENERACION_CUOTAS | 1 | Día de generación automática |
| DIA_VENCIMIENTO_CUOTAS | 15 | Día de vencimiento |

## Contribución

Este proyecto fue desarrollado como parte del Sistema de Gestión de Colegio Profesional.

## Soporte

Para problemas o preguntas, crear un issue en el repositorio.

## Licencia

Propietario: Colegio Profesional
Año: 2025
