# ---------- Stage 1: Maven build ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build

WORKDIR /build

# Копируем исходники
COPY . .

# Собираем DSpace
RUN mvn -B \
    -DskipTests \
    -Dmaven.wagon.http.retryHandler.count=5 \
    -Dmaven.wagon.http.pool=false \
    clean package

# ---------- Stage 2: Ant install ----------
FROM eclipse-temurin:17 AS ant_build

ARG TARGET_DIR=dspace-installer

WORKDIR /dspace-src

# Копируем dspace-installer из предыдущего слоя
COPY --from=build /build/dspace/target/${TARGET_DIR} /dspace-src

# Устанавливаем ant
RUN apt-get update && \
    apt-get install -y curl tar && \
    curl -L https://archive.apache.org/dist/ant/binaries/apache-ant-1.10.13-bin.tar.gz \
    | tar -xz -C /opt && \
    ln -s /opt/apache-ant-1.10.13/bin/ant /usr/bin/ant

# Выполняем установку DSpace
RUN ant init_installation update_configs update_code

# ---------- Stage 3: Runtime ----------
FROM eclipse-temurin:17

ENV DSPACE_INSTALL=/dspace
ENV JAVA_OPTS="-Xmx1000m"

# Копируем готовую установку
COPY --from=ant_build /dspace ${DSPACE_INSTALL}

# Устанавливаем unzip (нужно для AIP)
RUN apt-get update && \
    apt-get install -y --no-install-recommends unzip && \
    rm -rf /var/lib/apt/lists/*

WORKDIR ${DSPACE_INSTALL}

# По умолчанию запускаем REST API
CMD ["java", "-jar", "/dspace/webapps/server-boot.jar", "--dspace.dir=/dspace"]
