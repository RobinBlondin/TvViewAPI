FROM eclipse-temurin:21-jdk
WORKDIR /app

ARG CREDENTIALS_PATH
COPY app.jar app.jar
COPY ${CREDENTIALS_PATH} credentials.json

EXPOSE 8080
CMD ["java", "-jar", "app.jar"]
