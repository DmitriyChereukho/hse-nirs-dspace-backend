ARG JDK_VERSION=17
ARG DSPACE_VERSION=dspace-9_x
ARG DOCKER_REGISTRY=docker.io

# Step 1 - Run Maven Build
FROM ${DOCKER_REGISTRY}/dspace/dspace-dependencies:${DSPACE_VERSION} AS build
ARG TARGET_DIR=dspace-installer
WORKDIR /app

# Переключаемся на root для всей сборки
USER root

# Создаём директории
RUN mkdir -p /tmp/install

# Копируем исходники
ADD . /app/

# Запускаем Maven от root (у него нет проблем с правами)
ENV MAVEN_FLAGS="-P-test-environment -Denforcer.skip=true -Dcheckstyle.skip=true -Dlicense.skip=true -Dxml.skip=true"
RUN mvn --no-transfer-progress package ${MAVEN_FLAGS} && \
  mv /app/dspace/target/${TARGET_DIR}/* /tmp/install && \
  mvn clean

RUN rm -rf /tmp/install/webapps/server/

# Step 2 - Run Ant Deploy
FROM docker.io/eclipse-temurin:${JDK_VERSION} AS ant_build
ARG TARGET_DIR=dspace-installer
COPY --from=build /tmp/install /dspace-src
WORKDIR /dspace-src

ENV ANT_VERSION=1.10.13
ENV ANT_HOME=/tmp/ant-$ANT_VERSION
ENV PATH=$ANT_HOME/bin:$PATH

RUN mkdir $ANT_HOME && \
    curl --silent --show-error --location --fail --retry 5 --output /tmp/apache-ant.tar.gz \
      https://archive.apache.org/dist/ant/binaries/apache-ant-${ANT_VERSION}-bin.tar.gz && \
    tar -zx --strip-components=1 -f /tmp/apache-ant.tar.gz -C $ANT_HOME && \
    rm /tmp/apache-ant.tar.gz

RUN ant init_installation update_configs update_code update_webapps

# Step 3 - Start up DSpace via Runnable JAR
FROM docker.io/eclipse-temurin:${JDK_VERSION}
ENV DSPACE_INSTALL=/dspace
COPY --from=ant_build /dspace $DSPACE_INSTALL
WORKDIR $DSPACE_INSTALL

RUN apt-get update \
    && apt-get install -y --no-install-recommends host \
    && apt-get purge -y --auto-remove \
    && rm -rf /var/lib/apt/lists/*

EXPOSE 8080 8000
ENV JAVA_OPTS=-Xmx2000m
ENTRYPOINT ["java", "-jar", "webapps/server-boot.jar", "--dspace.dir=$DSPACE_INSTALL"]