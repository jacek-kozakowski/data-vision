FROM openjdk:21-jdk-slim

WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src


RUN chmod +x mvnw

RUN ./mvnw dependency:go-offline -B

RUN ./mvnw clean package -DskipTests
EXPOSE 8080

CMD ["java", "-jar", "target/backend-0.0.1-SNAPSHOT.jar"]