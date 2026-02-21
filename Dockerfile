#PROD

# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# ---- Runtime stage ----
FROM amazoncorretto:25.0.1
WORKDIR /app
COPY --from=build /app/target/subtitlescorrector-*.jar app.jar
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]
