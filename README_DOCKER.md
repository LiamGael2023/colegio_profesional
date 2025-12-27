# 🐳 Sistema de Colegio Profesional - Docker

Sistema completo de gestión para Colegios Profesionales ejecutándose en contenedores Docker.

## 📋 Requisitos Previos

- Docker Desktop instalado ([Descargar aquí](https://www.docker.com/products/docker-desktop))
- Docker Compose (incluido en Docker Desktop)

## 🚀 Inicio Rápido

### 1. Clonar el repositorio
```bash
git clone https://github.com/LiamGael2023/colegio_profesional.git
cd colegio_profesional
```

### 2. Levantar los contenedores
```bash
docker-compose up -d
```

Este comando levantará 3 servicios:
- **MySQL 8.0**: Base de datos en puerto `3306`
- **PHP 8.1 + Apache**: Aplicación web en puerto `8080`
- **phpMyAdmin**: Gestor de BD en puerto `8081`

### 3. Esperar a que la base de datos esté lista
```bash
docker-compose logs -f db
```
Espera a ver el mensaje: `mysqld: ready for connections`

### 4. Acceder al sistema
Abre tu navegador en:
```
http://localhost:8080
```

**Credenciales por defecto:**
- Usuario: `admin`
- Contraseña: `admin123`

## 🔧 Servicios Disponibles

| Servicio | URL | Descripción |
|----------|-----|-------------|
| **Aplicación Web** | http://localhost:8080 | Sistema principal |
| **phpMyAdmin** | http://localhost:8081 | Gestión de base de datos |
| **MySQL** | localhost:3306 | Base de datos directa |

## 📊 Gestión de Contenedores

### Ver estado de los contenedores
```bash
docker-compose ps
```

### Ver logs en tiempo real
```bash
# Todos los servicios
docker-compose logs -f

# Solo la aplicación web
docker-compose logs -f web

# Solo MySQL
docker-compose logs -f db
```

### Detener los contenedores
```bash
docker-compose down
```

### Detener y eliminar volúmenes (CUIDADO: borra la BD)
```bash
docker-compose down -v
```

### Reiniciar servicios
```bash
docker-compose restart
```

### Reconstruir contenedores después de cambios
```bash
docker-compose up -d --build
```

## 🗄️ Acceso a la Base de Datos

### Desde phpMyAdmin
1. Ir a http://localhost:8081
2. **Servidor**: `db`
3. **Usuario**: `root`
4. **Contraseña**: `rootpassword`

### Desde línea de comandos
```bash
docker exec -it colegio_mysql mysql -u root -prootpassword colegio_profesional
```

### Credenciales de la BD

**Root:**
- Usuario: `root`
- Contraseña: `rootpassword`

**Usuario de aplicación:**
- Usuario: `colegio_user`
- Contraseña: `colegio_pass`
- Base de datos: `colegio_profesional`

## 🔄 Importar/Exportar Datos

### Exportar base de datos
```bash
docker exec colegio_mysql mysqldump -u root -prootpassword colegio_profesional > backup.sql
```

### Importar base de datos
```bash
docker exec -i colegio_mysql mysql -u root -prootpassword colegio_profesional < backup.sql
```

## 🛠️ Desarrollo

### Modificar archivos PHP
Los archivos en `frontend-php/` están montados como volumen. Los cambios se reflejan inmediatamente sin necesidad de reiniciar el contenedor.

### Ver archivos dentro del contenedor
```bash
docker exec -it colegio_web bash
```

### Instalar extensiones PHP adicionales
Edita el `Dockerfile` y agrega:
```dockerfile
RUN docker-php-ext-install nombre_extension
```

Luego reconstruye:
```bash
docker-compose up -d --build
```

## 🐛 Solución de Problemas

### Error: "Port 8080 is already in use"
Cambia el puerto en `docker-compose.yml`:
```yaml
ports:
  - "8000:80"  # Cambiar 8080 por 8000
```

### Error: "Cannot connect to database"
1. Verifica que el contenedor de MySQL esté corriendo:
```bash
docker-compose ps
```

2. Verifica los logs de MySQL:
```bash
docker-compose logs db
```

3. Espera a que MySQL esté completamente iniciado (puede tardar 30-60 segundos la primera vez)

### Base de datos vacía
La base de datos se inicializa automáticamente con `database/schema.sql`. Si está vacía:
```bash
docker-compose down -v
docker-compose up -d
```

### Reiniciar desde cero
```bash
# Detener y eliminar todo
docker-compose down -v

# Eliminar imágenes (opcional)
docker rmi colegio_profesional-web

# Volver a levantar
docker-compose up -d --build
```

## 📝 Configuración Avanzada

### Cambiar credenciales de MySQL
Edita `docker-compose.yml`:
```yaml
environment:
  MYSQL_ROOT_PASSWORD: tu_password_seguro
  MYSQL_USER: tu_usuario
  MYSQL_PASSWORD: tu_password
```

### Persistencia de datos
Los datos de MySQL se guardan en un volumen Docker llamado `mysql_data`. Para respaldarlo:
```bash
docker volume inspect colegio_profesional_mysql_data
```

### Variables de entorno personalizadas
Crea un archivo `.env` en la raíz del proyecto:
```env
DB_HOST=db
DB_NAME=colegio_profesional
DB_USER=colegio_user
DB_PASS=colegio_pass
```

## 🔐 Seguridad

**IMPORTANTE para producción:**

1. Cambia todas las contraseñas por defecto
2. No expongas MySQL (puerto 3306) públicamente
3. Usa HTTPS con un reverse proxy (nginx)
4. Configura variables de entorno desde archivos secretos
5. Limita el acceso a phpMyAdmin

## 📦 Estructura del Proyecto

```
colegio_profesional/
├── docker-compose.yml          # Orquestación de contenedores
├── Dockerfile                  # Imagen de la aplicación PHP
├── .dockerignore              # Archivos ignorados por Docker
├── database/
│   └── schema.sql             # Esquema inicial de BD
└── frontend-php/              # Código fuente PHP (montado como volumen)
    ├── config/
    ├── controllers/
    ├── models/
    ├── views/
    └── ...
```

## 🎯 Características del Sistema

✅ **Gestión de Personas**: Alta, búsqueda por DNI, edición
✅ **Conversión a Colegiados**: Número de colegiatura, especialidades
✅ **Cuotas Automáticas**: Generación mensual (cron job pendiente)
✅ **Caja de Pagos**: Multi-obligación, recibos, historial
✅ **Habilitación Automática**: Basada en pagos al día
✅ **Reportes**: Estado de cuenta, deudas, recaudación

## 📞 Soporte

Para reportar problemas o solicitar ayuda, crea un issue en:
https://github.com/LiamGael2023/colegio_profesional/issues

## 📄 Licencia

Este proyecto es de uso educativo y profesional.

---

**Desarrollado con PHP 8.1, MySQL 8.0 y Docker** 🐘🐬🐳
