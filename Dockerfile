#PROD

# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# ---- Runtime stage ----
FROM amazoncorretto:25.0.1 # Use an official Java runtime as a parent image
WORKDIR /app # Set the working directory
COPY --from=build /app/target/subtitlescorrector-*.jar app.jar # Copy the application JAR file
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"] # Run the application
