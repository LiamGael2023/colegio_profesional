#!/bin/bash
set -e

echo "🚀 Iniciando aplicación Colegio Profesional..."

# Esperar a que MySQL esté disponible
echo "⏳ Esperando a que MySQL esté disponible..."
while ! mysqladmin ping -h"$DB_HOST" --silent; do
    echo "   MySQL no está listo todavía, esperando..."
    sleep 2
done

echo "✅ MySQL está disponible!"

# Verificar que la base de datos existe
echo "🔍 Verificando base de datos..."
mysql -h"$DB_HOST" -u"$DB_USER" -p"$DB_PASS" -e "USE $DB_NAME;" 2>/dev/null

if [ $? -eq 0 ]; then
    echo "✅ Base de datos '$DB_NAME' encontrada!"
else
    echo "⚠️  Base de datos '$DB_NAME' no encontrada. Por favor, verifica la configuración."
fi

echo "🌐 Iniciando servidor web Apache..."

# Ejecutar Apache en primer plano
exec apache2-foreground
