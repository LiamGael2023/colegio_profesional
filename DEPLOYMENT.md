# Guía de Despliegue - Sistema de Gestión de Colegio Profesional

Esta guía proporciona instrucciones detalladas para desplegar el sistema en un entorno de producción.

## Tabla de Contenidos
1. [Requisitos del Servidor](#requisitos-del-servidor)
2. [Instalación de Dependencias](#instalación-de-dependencias)
3. [Configuración de Base de Datos](#configuración-de-base-de-datos)
4. [Despliegue del Backend](#despliegue-del-backend)
5. [Despliegue del Frontend](#despliegue-del-frontend)
6. [Configuración de Seguridad](#configuración-de-seguridad)
7. [Monitoreo y Logs](#monitoreo-y-logs)

## Requisitos del Servidor

### Servidor de Aplicaciones (Backend)
- **OS**: Linux (Ubuntu 22.04 LTS recomendado)
- **RAM**: Mínimo 2GB, recomendado 4GB
- **CPU**: 2 cores mínimo
- **Disco**: 20GB mínimo
- **Java**: OpenJDK 17 o superior
- **Puerto**: 8080 (configurable)

### Servidor Web (Frontend)
- **OS**: Linux (Ubuntu 22.04 LTS recomendado)
- **RAM**: Mínimo 1GB
- **Servidor Web**: Apache 2.4+ o Nginx 1.18+
- **PHP**: 8.0 o superior
- **Extensiones PHP**: curl, json, mbstring

### Base de Datos
- **MySQL**: 8.0 o superior
- **RAM**: Mínimo 2GB
- **Disco**: 10GB mínimo (dependiendo del volumen de datos)

## Instalación de Dependencias

### 1. Instalar Java 17

```bash
# Ubuntu/Debian
sudo apt update
sudo apt install openjdk-17-jdk -y

# Verificar instalación
java -version
```

### 2. Instalar MySQL

```bash
# Ubuntu/Debian
sudo apt install mysql-server -y

# Configurar MySQL
sudo mysql_secure_installation

# Verificar instalación
sudo systemctl status mysql
```

### 3. Instalar Apache y PHP

```bash
# Ubuntu/Debian
sudo apt install apache2 php8.1 php8.1-cli php8.1-curl php8.1-mbstring -y

# Habilitar módulos de Apache
sudo a2enmod rewrite
sudo a2enmod headers

# Reiniciar Apache
sudo systemctl restart apache2
```

## Configuración de Base de Datos

### 1. Crear Usuario y Base de Datos

```bash
# Conectar a MySQL como root
sudo mysql -u root -p

# Dentro de MySQL
CREATE DATABASE colegio_profesional CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

CREATE USER 'colegio_user'@'localhost' IDENTIFIED BY 'password_seguro_aqui';
GRANT ALL PRIVILEGES ON colegio_profesional.* TO 'colegio_user'@'localhost';
FLUSH PRIVILEGES;

EXIT;
```

### 2. Importar el Schema

```bash
# Navegar al directorio del proyecto
cd /opt/colegio_profesional

# Importar schema
mysql -u colegio_user -p colegio_profesional < database/schema.sql
```

### 3. Verificar la Importación

```bash
mysql -u colegio_user -p

USE colegio_profesional;
SHOW TABLES;

# Verificar usuarios por defecto
SELECT username, rol FROM usuarios;

EXIT;
```

## Despliegue del Backend

### 1. Preparar el Proyecto

```bash
# Crear directorio de aplicación
sudo mkdir -p /opt/colegio_profesional/backend
cd /opt/colegio_profesional/backend

# Copiar el proyecto backend
# (Asumir que ya tienes el código en el servidor)
```

### 2. Configurar application.yml

```bash
# Editar configuración
sudo nano src/main/resources/application.yml
```

Actualizar las siguientes secciones:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/colegio_profesional
    username: colegio_user
    password: password_seguro_aqui

security:
  jwt:
    secret: "tu_clave_secreta_super_segura_de_al_menos_256_bits"
    expiration: 86400000  # 24 horas

logging:
  file:
    name: /var/log/colegio-profesional/application.log
```

### 3. Compilar el Proyecto

```bash
# Compilar con Maven
./mvnw clean package -DskipTests

# El JAR se generará en: target/colegio-profesional-backend-1.0.0.jar
```

### 4. Crear Servicio Systemd

```bash
# Crear archivo de servicio
sudo nano /etc/systemd/system/colegio-backend.service
```

Contenido del archivo:

```ini
[Unit]
Description=Colegio Profesional Backend API
After=mysql.service

[Service]
Type=simple
User=www-data
WorkingDirectory=/opt/colegio_profesional/backend
ExecStart=/usr/bin/java -jar /opt/colegio_profesional/backend/target/colegio-profesional-backend-1.0.0.jar
Restart=on-failure
RestartSec=10

StandardOutput=append:/var/log/colegio-profesional/backend.log
StandardError=append:/var/log/colegio-profesional/backend-error.log

[Install]
WantedBy=multi-user.target
```

### 5. Iniciar el Servicio

```bash
# Crear directorio de logs
sudo mkdir -p /var/log/colegio-profesional
sudo chown www-data:www-data /var/log/colegio-profesional

# Recargar systemd
sudo systemctl daemon-reload

# Habilitar e iniciar el servicio
sudo systemctl enable colegio-backend
sudo systemctl start colegio-backend

# Verificar el estado
sudo systemctl status colegio-backend

# Ver logs
sudo tail -f /var/log/colegio-profesional/backend.log
```

## Despliegue del Frontend

### 1. Configurar el Frontend

```bash
# Copiar el frontend a /var/www
sudo mkdir -p /var/www/colegio-profesional
sudo cp -r frontend-php/* /var/www/colegio-profesional/

# Establecer permisos
sudo chown -R www-data:www-data /var/www/colegio-profesional
sudo chmod -R 755 /var/www/colegio-profesional
```

### 2. Configurar Apache Virtual Host

```bash
# Crear archivo de configuración
sudo nano /etc/apache2/sites-available/colegio-profesional.conf
```

Contenido:

```apache
<VirtualHost *:80>
    ServerName colegio.example.com
    DocumentRoot /var/www/colegio-profesional/public

    <Directory /var/www/colegio-profesional/public>
        Options -Indexes +FollowSymLinks
        AllowOverride All
        Require all granted
    </Directory>

    ErrorLog ${APACHE_LOG_DIR}/colegio-error.log
    CustomLog ${APACHE_LOG_DIR}/colegio-access.log combined
</VirtualHost>
```

### 3. Habilitar el Sitio

```bash
# Deshabilitar sitio por defecto
sudo a2dissite 000-default.conf

# Habilitar nuestro sitio
sudo a2ensite colegio-profesional.conf

# Reiniciar Apache
sudo systemctl restart apache2
```

### 4. Configurar PHP

```bash
# Editar configuración de API en PHP
sudo nano /var/www/colegio-profesional/config/config.php
```

Actualizar:

```php
define('API_BASE_URL', 'http://localhost:8080/api');
define('API_AUTH_URL', 'http://localhost:8080/api/auth');
```

## Configuración de Seguridad

### 1. Configurar Firewall

```bash
# UFW (Ubuntu)
sudo ufw allow 80/tcp
sudo ufw allow 443/tcp
sudo ufw allow 3306/tcp  # Solo si MySQL está en otro servidor
sudo ufw enable
```

### 2. Configurar SSL/TLS (Recomendado)

```bash
# Instalar Certbot
sudo apt install certbot python3-certbot-apache -y

# Obtener certificado SSL
sudo certbot --apache -d colegio.example.com

# El certificado se renovará automáticamente
```

### 3. Securizar MySQL

```bash
# Limitar acceso remoto
sudo nano /etc/mysql/mysql.conf.d/mysqld.cnf

# Agregar/verificar:
bind-address = 127.0.0.1

# Reiniciar MySQL
sudo systemctl restart mysql
```

### 4. Cambiar Contraseñas por Defecto

```sql
-- Conectar a MySQL
mysql -u root -p

USE colegio_profesional;

-- Actualizar contraseñas de usuarios (ya hasheadas con BCrypt)
-- Generar hash en: https://bcrypt-generator.com/
UPDATE usuarios SET password = '$2a$10$NUEVO_HASH_AQUI' WHERE username = 'admin';
UPDATE usuarios SET password = '$2a$10$NUEVO_HASH_AQUI' WHERE username = 'cajero';
```

### 5. Cambiar JWT Secret

```bash
# Generar un secreto aleatorio seguro
openssl rand -base64 64

# Actualizar en application.yml
sudo nano /opt/colegio_profesional/backend/src/main/resources/application.yml
```

## Monitoreo y Logs

### 1. Logs del Backend

```bash
# Ver logs en tiempo real
sudo journalctl -u colegio-backend -f

# Ver logs de aplicación
sudo tail -f /var/log/colegio-profesional/backend.log
```

### 2. Logs de Apache

```bash
# Logs de acceso
sudo tail -f /var/log/apache2/colegio-access.log

# Logs de errores
sudo tail -f /var/log/apache2/colegio-error.log
```

### 3. Logs de MySQL

```bash
# Ver logs de error
sudo tail -f /var/log/mysql/error.log
```

### 4. Monitoreo de Recursos

```bash
# Uso de CPU y memoria
htop

# Espacio en disco
df -h

# Estado de servicios
sudo systemctl status colegio-backend
sudo systemctl status apache2
sudo systemctl status mysql
```

## Backups

### Script de Backup Automático

```bash
# Crear script de backup
sudo nano /opt/colegio_profesional/backup.sh
```

Contenido:

```bash
#!/bin/bash

BACKUP_DIR="/opt/backups/colegio"
DATE=$(date +%Y%m%d_%H%M%S)

mkdir -p $BACKUP_DIR

# Backup de base de datos
mysqldump -u colegio_user -p'password_seguro_aqui' colegio_profesional | gzip > $BACKUP_DIR/db_$DATE.sql.gz

# Eliminar backups antiguos (más de 30 días)
find $BACKUP_DIR -name "db_*.sql.gz" -mtime +30 -delete

echo "Backup completado: $DATE"
```

```bash
# Hacer ejecutable
sudo chmod +x /opt/colegio_profesional/backup.sh

# Programar en crontab (diario a las 2 AM)
sudo crontab -e

# Agregar línea:
0 2 * * * /opt/colegio_profesional/backup.sh >> /var/log/colegio-profesional/backup.log 2>&1
```

## Verificación Final

### 1. Verificar Backend

```bash
curl http://localhost:8080/api/auth/health
```

Respuesta esperada:
```json
{
  "status": "UP",
  "service": "Colegio Profesional API",
  "timestamp": 1234567890
}
```

### 2. Verificar Frontend

Abrir en navegador:
```
http://colegio.example.com/login.php
```

### 3. Probar Login

Credenciales por defecto:
- Usuario: `admin`
- Contraseña: `admin123` (cambiar en producción)

## Troubleshooting

### Backend no inicia

```bash
# Ver logs detallados
sudo journalctl -u colegio-backend -n 100 --no-pager

# Verificar conectividad con MySQL
mysql -u colegio_user -p -h localhost
```

### Frontend no se conecta al Backend

```bash
# Verificar que el backend esté corriendo
sudo systemctl status colegio-backend

# Verificar URL en config.php
cat /var/www/colegio-profesional/config/config.php | grep API_BASE_URL
```

### Error de permisos en PHP

```bash
# Restablecer permisos
sudo chown -R www-data:www-data /var/www/colegio-profesional
sudo chmod -R 755 /var/www/colegio-profesional
```

## Contacto y Soporte

Para asistencia técnica, contactar al equipo de desarrollo.
