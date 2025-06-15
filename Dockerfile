FROM maven:3-openjdk-17-slim AS build

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline

COPY src ./src
RUN mvn clean package -Dmaven.test.skip=true

FROM openjdk:17-jdk-alpine

WORKDIR /app

COPY --from=build /app/target/orders-0.0.1-SNAPSHOT.jar app.jar

COPY pedidos.jsonl .

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "app.jar"]