########################
# Build stage
########################
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn -B clean package -DskipTests

########################
# Runtime stage
########################
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
COPY --from=build /app/target/card-service.jar /app.jar
EXPOSE 8083
ENTRYPOINT ["java","-jar","/app.jar"]
