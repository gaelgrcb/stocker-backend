# Fase 1: Construcción (Build)
# Usamos una imagen de Gradle con Java 21 para compilar
FROM gradle:8.5-jdk21 AS build
COPY --chown=gradle:gradle . /home/gradle/src
WORKDIR /home/gradle/src
# Ejecutamos el build omitiendo los tests para acelerar el despliegue en Render
RUN gradle build -x test --no-daemon

# Fase 2: Ejecución (Runtime)
# Usamos una imagen ligera de Java 21 para correr la app
FROM openjdk:21-jdk-slim
EXPOSE 8080
# Copiamos el .jar generado desde la fase anterior
COPY --from=build /home/gradle/src/build/libs/*.jar app.jar

# Comando para ejecutar la aplicación
ENTRYPOINT ["java", "-jar", "/app.jar"]