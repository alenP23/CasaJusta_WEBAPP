#!/bin/bash

DB_HOST="localhost"
DB_USER="root"
DB_PASSWORD="pasahitza"
DB_NAME="CasaJusta"
BACKUP_DIR="/mnt/c/Users/alen_p/Desktop/backups"
TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILENAME="backup_casajusta_${TIMESTAMP}.sql"

mkdir -p $BACKUP_DIR

# Usamos el nombre real del contenedor (o el ID, los dos funcionan)
docker exec spring-kaixo-mundua_devcontainer-mysql-1 mysqldump -h $DB_HOST -u $DB_USER -p$DB_PASSWORD $DB_NAME > "$BACKUP_DIR/$BACKUP_FILENAME"

echo "✅ Backup guardado en: $BACKUP_DIR/$BACKUP_FILENAME"
