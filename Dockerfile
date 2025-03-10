FROM eclipse-temurin:21-jdk

WORKDIR /app

COPY app.jar app.jar
COPY .env .env

EXPOSE 8080

CMD ["java", "-jar", "app.jar"]
