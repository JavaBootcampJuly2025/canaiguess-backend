# ---- Stage 1: Build ----
FROM maven:3.9.6-eclipse-temurin-21 AS build

# Set working dir inside builder container
WORKDIR /build

# Copy your Maven project into the image
COPY pom.xml .
COPY src ./src

# Build the app (creates target/*.jar)
RUN mvn clean package

# ---- Stage 2: Runtime ----
FROM amazoncorretto:21

# Set workdir inside runtime container
WORKDIR /app

# Copy the jar from the build container
COPY --from=build /build/target/*.jar app.jar

# Run the app
ENTRYPOINT ["java", "-jar", "app.jar"]

