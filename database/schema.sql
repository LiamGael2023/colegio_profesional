-- ============================================
-- SISTEMA DE GESTIÓN DE COLEGIO PROFESIONAL
-- Base de Datos MySQL
-- ============================================

DROP DATABASE IF EXISTS colegio_profesional;
CREATE DATABASE colegio_profesional CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE colegio_profesional;

-- ============================================
-- TABLA: personas
-- Almacena toda persona que interactúa con el sistema
-- Puede ser "Público General" o "Colegiado"
-- ============================================
CREATE TABLE personas (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dni VARCHAR(20) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellido_paterno VARCHAR(100) NOT NULL,
    apellido_materno VARCHAR(100),
    email VARCHAR(150),
    telefono VARCHAR(20),
    direccion VARCHAR(255),
    fecha_nacimiento DATE,
    tipo_persona ENUM('PUBLICO', 'COLEGIADO') NOT NULL DEFAULT 'PUBLICO',
    fecha_registro DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_dni (dni),
    INDEX idx_tipo_persona (tipo_persona),
    INDEX idx_activo (activo)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: colegiados
-- Extensión de personas para colegiados
-- Relación 1:1 con personas
-- ============================================
CREATE TABLE colegiados (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL UNIQUE,
    numero_colegiatura VARCHAR(20) NOT NULL UNIQUE,
    especialidad VARCHAR(100),
    universidad VARCHAR(150),
    fecha_colegiatura DATE NOT NULL,
    estado_habilitacion ENUM('HABILITADO', 'INHABILITADO', 'SUSPENDIDO') NOT NULL DEFAULT 'HABILITADO',
    meses_tolerancia_impago INT NOT NULL DEFAULT 3,
    meses_impagos_consecutivos INT NOT NULL DEFAULT 0,
    observaciones TEXT,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    INDEX idx_numero_colegiatura (numero_colegiatura),
    INDEX idx_estado_habilitacion (estado_habilitacion),
    INDEX idx_persona_id (persona_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: aportaciones
-- Registra las obligaciones/deudas mensuales
-- UNIQUE KEY compuesta evita duplicidad
-- ============================================
CREATE TABLE aportaciones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    mes INT NOT NULL CHECK (mes BETWEEN 1 AND 12),
    anio INT NOT NULL CHECK (anio >= 2000),
    monto DECIMAL(10, 2) NOT NULL,
    concepto VARCHAR(255) NOT NULL DEFAULT 'Cuota mensual de colegiatura',
    estado ENUM('PENDIENTE', 'PAGADO', 'ANULADO') NOT NULL DEFAULT 'PENDIENTE',
    fecha_vencimiento DATE,
    fecha_generacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    tipo_generacion ENUM('AUTOMATICA', 'MANUAL') NOT NULL DEFAULT 'AUTOMATICA',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    UNIQUE KEY unique_aportacion (persona_id, mes, anio),
    INDEX idx_persona_estado (persona_id, estado),
    INDEX idx_mes_anio (mes, anio),
    INDEX idx_estado (estado),
    INDEX idx_fecha_vencimiento (fecha_vencimiento)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: pagos
-- Registra los pagos realizados
-- Un pago puede cubrir múltiples aportaciones
-- ============================================
CREATE TABLE pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    persona_id BIGINT NOT NULL,
    monto_total DECIMAL(10, 2) NOT NULL,
    metodo_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'CHEQUE', 'YAPE', 'PLIN') NOT NULL,
    numero_comprobante VARCHAR(50),
    tipo_comprobante ENUM('BOLETA', 'FACTURA', 'RECIBO') NOT NULL DEFAULT 'RECIBO',
    observaciones TEXT,
    fecha_pago DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    usuario_registro VARCHAR(100),
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (persona_id) REFERENCES personas(id) ON DELETE CASCADE,
    INDEX idx_persona_id (persona_id),
    INDEX idx_fecha_pago (fecha_pago),
    INDEX idx_numero_comprobante (numero_comprobante)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: detalle_pagos
-- Detalle de qué aportaciones cubre cada pago
-- Relación N:N entre pagos y aportaciones
-- ============================================
CREATE TABLE detalle_pagos (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pago_id BIGINT NOT NULL,
    aportacion_id BIGINT NOT NULL,
    monto_aplicado DECIMAL(10, 2) NOT NULL,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (pago_id) REFERENCES pagos(id) ON DELETE CASCADE,
    FOREIGN KEY (aportacion_id) REFERENCES aportaciones(id) ON DELETE CASCADE,
    UNIQUE KEY unique_detalle (pago_id, aportacion_id),
    INDEX idx_pago_id (pago_id),
    INDEX idx_aportacion_id (aportacion_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: usuarios
-- Usuarios del sistema (administradores, cajeros, etc.)
-- ============================================
CREATE TABLE usuarios (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    rol ENUM('ADMIN', 'CAJERO', 'SECRETARIA', 'AUDITOR') NOT NULL DEFAULT 'CAJERO',
    activo BOOLEAN NOT NULL DEFAULT TRUE,
    ultimo_acceso DATETIME,
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_username (username),
    INDEX idx_email (email),
    INDEX idx_rol (rol)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: configuracion
-- Parámetros de configuración del sistema
-- ============================================
CREATE TABLE configuracion (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    clave VARCHAR(100) NOT NULL UNIQUE,
    valor TEXT NOT NULL,
    descripcion VARCHAR(255),
    tipo_dato ENUM('STRING', 'NUMBER', 'BOOLEAN', 'DATE') NOT NULL DEFAULT 'STRING',
    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_clave (clave)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- TABLA: auditoria
-- Log de operaciones importantes del sistema
-- ============================================
CREATE TABLE auditoria (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    usuario_id BIGINT,
    accion VARCHAR(100) NOT NULL,
    tabla_afectada VARCHAR(50),
    registro_id BIGINT,
    datos_anteriores JSON,
    datos_nuevos JSON,
    ip_address VARCHAR(45),
    user_agent VARCHAR(255),
    fecha_hora DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE SET NULL,
    INDEX idx_usuario_id (usuario_id),
    INDEX idx_fecha_hora (fecha_hora),
    INDEX idx_accion (accion),
    INDEX idx_tabla_registro (tabla_afectada, registro_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================
-- DATOS INICIALES
-- ============================================

-- Configuraciones del sistema
INSERT INTO configuracion (clave, valor, descripcion, tipo_dato) VALUES
('CUOTA_MENSUAL_DEFAULT', '150.00', 'Monto de cuota mensual por defecto', 'NUMBER'),
('MESES_TOLERANCIA_IMPAGO', '3', 'Meses de tolerancia antes de inhabilitar', 'NUMBER'),
('DIA_GENERACION_CUOTAS', '1', 'Día del mes para generar cuotas automáticamente', 'NUMBER'),
('DIA_VENCIMIENTO_CUOTAS', '15', 'Día del mes de vencimiento de cuotas', 'NUMBER'),
('JWT_SECRET', 'colegio_profesional_secret_key_2025', 'Clave secreta para JWT', 'STRING'),
('JWT_EXPIRATION_HOURS', '24', 'Horas de expiración del token JWT', 'NUMBER'),
('NOMBRE_COLEGIO', 'Colegio Profesional', 'Nombre del colegio', 'STRING'),
('EMAIL_NOTIFICACIONES', 'notificaciones@colegio.com', 'Email para notificaciones', 'STRING');

-- Usuario administrador por defecto (password: admin123)
-- Contraseña hasheada con BCrypt
INSERT INTO usuarios (username, password, email, nombres, apellidos, rol, activo) VALUES
('admin', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'admin@colegio.com', 'Administrador', 'Sistema', 'ADMIN', TRUE),
('cajero', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'cajero@colegio.com', 'Cajero', 'Principal', 'CAJERO', TRUE);

-- Datos de ejemplo (opcional, comentar si no se desean)
-- Persona del público general
INSERT INTO personas (dni, nombres, apellido_paterno, apellido_materno, email, telefono, tipo_persona) VALUES
('12345678', 'Juan', 'Pérez', 'García', 'juan.perez@email.com', '987654321', 'PUBLICO');

-- Persona colegiada
INSERT INTO personas (dni, nombres, apellido_paterno, apellido_materno, email, telefono, tipo_persona, fecha_nacimiento) VALUES
('87654321', 'María', 'López', 'Martínez', 'maria.lopez@email.com', '987123456', 'COLEGIADO', '1990-05-15'),
('11223344', 'Carlos', 'Ramírez', 'Torres', 'carlos.ramirez@email.com', '987456789', 'COLEGIADO', '1988-08-20');

-- Registros de colegiados
INSERT INTO colegiados (persona_id, numero_colegiatura, especialidad, universidad, fecha_colegiatura, estado_habilitacion) VALUES
(2, 'COL-2020-001', 'Ingeniería Civil', 'Universidad Nacional', '2020-03-01', 'HABILITADO'),
(3, 'COL-2019-045', 'Arquitectura', 'Universidad Técnica', '2019-06-15', 'HABILITADO');

-- ============================================
-- TRIGGERS
-- ============================================

-- Trigger para actualizar tipo_persona cuando se crea un colegiado
DELIMITER //
CREATE TRIGGER after_colegiado_insert
AFTER INSERT ON colegiados
FOR EACH ROW
BEGIN
    UPDATE personas SET tipo_persona = 'COLEGIADO' WHERE id = NEW.persona_id;
END//

-- Trigger para actualizar estado de aportación cuando se registra un pago
CREATE TRIGGER after_detalle_pago_insert
AFTER INSERT ON detalle_pagos
FOR EACH ROW
BEGIN
    UPDATE aportaciones SET estado = 'PAGADO' WHERE id = NEW.aportacion_id;
END//

DELIMITER ;

-- ============================================
-- VISTAS ÚTILES
-- ============================================

-- Vista de colegiados con datos de persona
CREATE VIEW vista_colegiados AS
SELECT
    c.id AS colegiado_id,
    c.numero_colegiatura,
    c.especialidad,
    c.estado_habilitacion,
    c.meses_impagos_consecutivos,
    p.id AS persona_id,
    p.dni,
    CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', IFNULL(p.apellido_materno, '')) AS nombre_completo,
    p.email,
    p.telefono,
    c.fecha_colegiatura
FROM colegiados c
INNER JOIN personas p ON c.persona_id = p.id
WHERE p.activo = TRUE;

-- Vista de deudas por persona
CREATE VIEW vista_deudas AS
SELECT
    p.id AS persona_id,
    p.dni,
    CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', IFNULL(p.apellido_materno, '')) AS nombre_completo,
    COUNT(a.id) AS total_deudas,
    SUM(a.monto) AS monto_total_deuda,
    MIN(CONCAT(a.anio, '-', LPAD(a.mes, 2, '0'), '-01')) AS deuda_mas_antigua
FROM personas p
INNER JOIN aportaciones a ON p.id = a.persona_id
WHERE a.estado = 'PENDIENTE'
GROUP BY p.id, p.dni, nombre_completo;

-- Vista de estado de cuenta por persona
CREATE VIEW vista_estado_cuenta AS
SELECT
    p.id AS persona_id,
    p.dni,
    CONCAT(p.nombres, ' ', p.apellido_paterno, ' ', IFNULL(p.apellido_materno, '')) AS nombre_completo,
    a.mes,
    a.anio,
    a.monto,
    a.concepto,
    a.estado,
    a.fecha_vencimiento,
    IFNULL(pg.fecha_pago, NULL) AS fecha_pago,
    IFNULL(pg.numero_comprobante, NULL) AS numero_comprobante
FROM personas p
INNER JOIN aportaciones a ON p.id = a.persona_id
LEFT JOIN detalle_pagos dp ON a.id = dp.aportacion_id
LEFT JOIN pagos pg ON dp.pago_id = pg.id
ORDER BY p.id, a.anio DESC, a.mes DESC;

-- ============================================
-- FIN DEL SCRIPT
-- ============================================
