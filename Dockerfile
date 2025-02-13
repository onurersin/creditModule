# 1st Stage: Build the Java Project using Maven
FROM maven:3.8.7-eclipse-temurin-17 AS build

# Set working directory inside the container
WORKDIR /app

# Copy pom.xml and dependencies first (to leverage Docker cache)
COPY pom.xml .

# Download dependencies
RUN mvn dependency:go-offline

# Copy the entire project
COPY . .

# Build the application
RUN mvn clean package -DskipTests

# 2nd Stage: Run the Built Application
FROM openjdk:17-jdk-slim

# Set working directory for runtime
WORKDIR /app

# Copy the built JAR from the previous stage
COPY --from=build /app/target/*.jar app.jar

# Copy H2 Database JAR
RUN mkdir -p /app/h2
COPY h2.jar /app/h2/h2.jar

# Copy database initialization script
COPY init-db.sql /app/init-db.sql

# Expose ports for the application and H2 database
EXPOSE 8080 8082 9092

# Start the H2 database and initialize it
CMD java -cp /app/h2/h2.jar org.h2.tools.Server -tcp -tcpAllowOthers -tcpPort 9092 -web -webAllowOthers -webPort 8082 & \
    sleep 5 && \
    java -cp "/app/h2/h2.jar" org.h2.tools.Shell -url jdbc:h2:/app/data/mydb -user sa -password "" -sql "CREATE SCHEMA IF NOT EXISTS PUBLIC;" && \
    java -cp "/app/h2/h2.jar" org.h2.tools.RunScript -url jdbc:h2:tcp://localhost:9092/./data/mydb -script /app/init-db.sql -user sa -password "" && \
    java -jar app.jar
