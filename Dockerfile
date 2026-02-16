#PROD

# ---- Build stage ----
FROM maven:3.9.6-eclipse-temurin-21 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests


# ---- Runtime stage ----
# Use an official Java runtime as a parent image
FROM amazoncorretto:25.0.1

# Set the working directory
WORKDIR /app

# Copy the application JAR file
COPY ./target/subtitlescorrector-*.jar app.jar

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar"]