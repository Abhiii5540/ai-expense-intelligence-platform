FROM maven:3.9.10-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
RUN mvn -B -DskipTests dependency:go-offline
COPY src ./src
RUN mvn -B clean package -DskipTests

FROM eclipse-temurin:21-jre
WORKDIR /app
RUN useradd --system --uid 1001 spring
COPY --from=build /app/target/expense-ai-service-1.0.0.jar app.jar
USER spring
EXPOSE 8000
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
