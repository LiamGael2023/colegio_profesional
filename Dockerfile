FROM php:8.1-apache

# Instalar dependencias del sistema
RUN apt-get update && apt-get install -y \
    default-mysql-client \
    && rm -rf /var/lib/apt/lists/*

# Instalar extensiones PHP necesarias
RUN docker-php-ext-install pdo pdo_mysql mysqli

# Habilitar mod_rewrite y mod_expires de Apache
RUN a2enmod rewrite expires deflate

# Configurar el DocumentRoot
ENV APACHE_DOCUMENT_ROOT=/var/www/html/frontend-php

# Actualizar configuración de Apache
RUN sed -ri -e 's!/var/www/html!${APACHE_DOCUMENT_ROOT}!g' /etc/apache/sites-available/*.conf
RUN sed -ri -e 's!/var/www/!${APACHE_DOCUMENT_ROOT}!g' /etc/apache/apache2.conf /etc/apache/conf-available/*.conf

# Copiar aplicación
COPY --chown=www-data:www-data . /var/www/html/

# Configurar directivas de Apache para permitir .htaccess
RUN echo '<Directory /var/www/html/frontend-php>\n\
    Options -Indexes +FollowSymLinks\n\
    AllowOverride All\n\
    Require all granted\n\
</Directory>' > /etc/apache2/conf-available/colegio.conf

RUN a2enconf colegio

# Copiar y dar permisos al script de entrypoint
COPY docker-entrypoint.sh /usr/local/bin/
RUN chmod +x /usr/local/bin/docker-entrypoint.sh

# Configurar permisos
RUN chown -R www-data:www-data /var/www/html

WORKDIR /var/www/html/frontend-php

# Exponer puerto 80
EXPOSE 80

# Usar el script de entrypoint personalizado
ENTRYPOINT ["docker-entrypoint.sh"]
