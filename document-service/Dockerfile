FROM maven:3.9.6-eclipse-temurin-21 AS builder
WORKDIR /build

# Copy parent pom.xml (one level up, passed via docker-compose context)
COPY pom.xml parent-pom.xml

# Copy this service module
COPY eureka-server/ eureka-server/

WORKDIR /build/eureka-server
RUN mvn clean package -DskipTests -B -f pom.xml \
    -Dmaven.repo.local=/root/.m2/repository

FROM eclipse-temurin:17-jre-alpine
WORKDIR /app
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
COPY --from=builder /build/eureka-server/target/*.jar app.jar
USER appuser
ENTRYPOINT ["java","-XX:+UseContainerSupport","-XX:MaxRAMPercentage=75.0","-jar","app.jar"]