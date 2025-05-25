# Imagen base de Maven con JDK 17
FROM maven:3.8.5-openjdk-17 AS builder

# Establecer el directorio de trabajo
WORKDIR /app

# Copiar el contenido del proyecto
COPY . .

# Dar permisos de ejecución al wrapper de Maven
RUN chmod +x mvnw

# Construir el proyecto
RUN ./mvnw clean package -DskipTests

# Segunda etapa: imagen ligera de Java para ejecutar el jar
FROM openjdk:17-jdk-slim

WORKDIR /app

# Copiar el .jar generado desde la fase anterior
COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Ejecutar la aplicación
CMD ["java", "-jar", "app.jar"]
