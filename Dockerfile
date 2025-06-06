# Dockerfile
FROM openjdk:17-jdk
WORKDIR /app
COPY docker/app/app.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]