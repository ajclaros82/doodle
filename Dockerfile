# Build stage
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /workspace

# Copy pom first to leverage Docker layer caching
COPY pom.xml .
RUN mvn -q -DskipTests dependency:go-offline

# Copy sources
COPY src ./src

# Build an executable Spring Boot fat jar
RUN mvn -q -DskipTests package && cp target/*.jar /workspace/app.jar

# Runtime stage
FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=build /workspace/app.jar /app/app.jar

EXPOSE 8080

ENV JAVA_OPTS=""

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar /app/app.jar"]
