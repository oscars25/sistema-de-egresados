# Usamos una imagen oficial de OpenJDK para construir y correr la app
FROM openjdk:17-jdk-slim

# Directorio de trabajo dentro del contenedor
WORKDIR /app

# Copiamos el archivo jar generado por Maven
COPY target/egresados-0.0.1-SNAPSHOT.jar app.jar

# Exponemos el puerto en el que corre la app
EXPOSE 8080

# Comando para ejecutar la app
ENTRYPOINT ["java", "-jar", "app.jar"]
