# ============================================
# Stage 1: Maven build (сборка исходников)
# ============================================
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build

# Копируем исходники DSpace
COPY . .

# Собираем DSpace (без тестов для ускорения)
RUN mvn -B \
    -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.pool=false \
    clean package

# ============================================
# Stage 2: Ant install (установка DSpace)
# ============================================
FROM eclipse-temurin:17-jdk AS ant_build

ARG TARGET_DIR=dspace-installer

WORKDIR /dspace-src

# Копируем собранный installer из предыдущего этапа
COPY --from=build /build/dspace/target/${TARGET_DIR} /dspace-src

# Устанавливаем Ant
RUN apt-get update && \
    apt-get install -y curl tar && \
    curl -L https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.13-bin.tar.gz \
    | tar -xz -C /opt && \
    ln -s /opt/apache-ant-1.10.13/bin/ant /usr/bin/ant && \
    rm -rf /var/lib/apt/lists/*

# Устанавливаем DSpace в /dspace
RUN ant init_installation update_configs update_code

# ============================================
# Stage 3: Runtime (Tomcat + DSpace)
# ============================================
FROM tomcat:9-jdk17-temurin

ENV DSPACE_INSTALL=/dspace
ENV JAVA_OPTS="-Xmx1000m -Dfile.encoding=UTF-8"
ENV CATALINA_OPTS="-Ddspace.dir=${DSPACE_INSTALL}"

# Устанавливаем необходимые утилиты
RUN apt-get update && \
    apt-get install -y --no-install-recommends \
        postgresql-client \
        unzip \
        && \
    rm -rf /var/lib/apt/lists/*

# Копируем установленный DSpace из предыдущего этапа
COPY --from=ant_build /dspace ${DSPACE_INSTALL}

# Копируем webapps DSpace в Tomcat
# Очищаем стандартные webapps Tomcat и копируем наши
RUN rm -rf /usr/local/tomcat/webapps/* && \
    cp -r ${DSPACE_INSTALL}/webapps/* /usr/local/tomcat/webapps/

# Копируем скрипт для ожидания БД и миграции
COPY --chmod=755 <<-'EOF' /entrypoint.sh
#!/bin/bash
set -e

echo "Waiting for PostgreSQL at ${DB_URL:=jdbc:postgresql://dspacedb:5432/dspace}"

# Извлекаем хост и порт из JDBC URL
DB_HOST=$(echo ${DB_URL} | sed -n 's/.*\/\/\([^:]*\):.*/\1/p')
DB_PORT=$(echo ${DB_URL} | sed -n 's/.*:\([0-9]\+\)\/.*/\1/p')

# Ждём готовности БД
while (!</dev/tcp/${DB_HOST}/${DB_PORT}) > /dev/null 2>&1; do
    echo "Waiting for database at ${DB_HOST}:${DB_PORT}..."
    sleep 1
done

echo "Database is ready!"

# Выполняем миграцию БД
/dspace/bin/dspace database migrate

echo "Database migration completed!"

# Запускаем Tomcat
exec catalina.sh run
EOF

WORKDIR ${DSPACE_INSTALL}

EXPOSE 8080

ENTRYPOINT ["/entrypoint.sh"]